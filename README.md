_This is work in progress!_

# How Far can we Take Kotlin DSLs? Yes!

Is it possible to build a full "inner" programming language as a
Kotlin DSL? For science, we have to find out! 



## Expressions

Before we start our DSL, let's quickly build up expressions as a building block.


Let's assume our languages uses an interface `Expr` to represent expressions that we can evaluate in some context that we'll come back to later: 


```kt
class Context {
    // We'll get to this later...
}

interface Expr {
    fun eval(context: Context): Any

    // Convenience 

    fun evalDouble(context: Context) = (eval(context) as Number).toDouble()

    fun evalBoolean(context: Context) {
        val v = eval(context)
        return v == true || (v is Number && v.toDouble() != 0.0)
    }

}
```

### Literals

For literals, we can define a class `Literal` as follows:

```kt
class Literal(val value: Any) : Expr {
    override fun eval(context: Context) = value

    override fun toString() = "Literal($value)"
}
```


### Expression Trees 

Operator overloading allows us to recursively build an 
expression tree with regular operators and operator precedence.

First, we define a binary expression node that is able
to do some math:

```kt
class BinaryOperator(
    val op: String, 
    val left: Expr, 
    val right: Expr
): Expr {
    fun eval(context: Context) {
        val l = left.evalDouble(context)
        val r = right.evalDouble(context)
        return when (op) {
            "+" -> l + r
            "*" -> l * r
            // ...
            else -> throw UnsupportedOperationException(op)
        }
    }

    fun toString() = "($left $op $right)"
}
```

Then we use operator overloading to enable building expression trees with the corresponding operators.

```kt
interface Expr {
    operator fun Expr.add(other: Expr) = 
        BinaryOperator("+", this, other)

    operator fun Expr.mul(other: Expr) = 
        BinaryOperator("-", this, other) 

    // ...
}
```

Now we can build an expression that adds two numbers with the following
code snippet:

```kt
val addExpr = Literal(40) + Literal(2)
```

As we have implemented toString()-funcitons for
all our expressions classes, we can verify that 
the expression is constructed correctly via

```kt
println(addExpr)
```

The output should be 
```kt
(Literal(40) + Literal(2))
````

Now we test if this works as expected by evaluating the expression:
```kt
print(addExpr.eval(Context()))
```
Which should just print `42.0`.

Of course there is a simpler way to calculate the sum of
40 and 2. The important difference here is that we have generated an expression tree that we can inspect and manipulate and where we can repeat the included operations if we want to.

For instance, after adding variables to our expressions, we could implement symbolic derivation. 


## Blocks 

With basic expressions in place,
we can start working on our "actual" DSL.

First, we define a class `Block` that bundles multiple expressions and exectutes
them sequentially:

```kt
class Block(val statements: List<Expr>) : Expr {
    override fun eval(context: Context) {
        for (statement in statemnts) {
            statement.eval(context)
        }
    }
}
```

Now, we add a builder for these blocks,
adding a `PrintLn` function on the way.

To be able to easily distinguish our "inner"
language from Kotlin, we'll start our 
identifiers with a capitalized letter.


```kt
class BlockBuilder {
    var statements: mutableListOf<Expr>()

    class BlockBuilder {
    var statements = mutableListOf<Expr>()

    fun Print(expr: Expr) {
        statements.add(
            object : Expr {
                override fun eval(context: Context) {
                    println(expr.eval(context))
                }
            }
        )
    }

    fun build() = Block(statements.toList())
}
```

Finally, we create the DSL to build these
blocks:

```kt
fun block(init: BlockBuilder.() -> Unit): Block {
    val builder = BlockBuilder()
    init(builder)
    return builder.build()
}
```

Now we can use what we have defined so far and create our first block of code, which should just print "42" again, with the "print"
command moved to our inner language now.

```kt
  val b = block {
    PrintLn(Const(40) + Const(2))
  }

  b.eval(Context())
```


## Variables

We'll implement variables by storing then in an array in our Context object:

```kt
class Context(size: Int = 0) {
    val variables = Array<Any>(size) { Unit }
}
```

To access variables in expressions, we'll just read the value at the corresponding index 
from the context:

```kt
class VarRef(val index: Int) : Expr {
    override fun eval(context: Context) = context.variables[index]

    override fun toString() = "var$index"
}
```

With this in place, we still need something to assign values to variables --
and some wireup in our block builder:

```kt
class VarAssignment(
    val declaration: Boolean,
    val index: Int,
    val value: Expr
) : Expr {
    override fun eval(context: Context) {
        context.variables[index] = value.eval(context)
    }
    
    override fun toString() = 
        if (declaration) "val var$index = $value" else "Set(var$index, $value)"
}

class BlockBuilder(var varCount: Int = 0) {
    var statements = mutableListOf<Expr>()

    fun Var(initialValue: Expr): VarRef {
        val index = varCount++
        statements.add(VarAssignment(true, index, initialValue))
        return VarRef(index)
    }

    fun Set(target: VarRef, value: Expr) {
        statements.add(VarAssignment(false, target.index, value))
    }

    // PrintLn definition from above etc.
}
```

Now we can use variables like this:

```
val myVar = Var(Literal(40))
Set(myVar, myVar + Literal(2))
PrintLn(myVar)
```

### Simplifying Literals

We can avoid some of the explicit `Literal()` calls
by automatically turning value into literals where we
know an expression is expected. For this,
we expand out expression class as follows:

```kt
interface Expr {
    fun eval(context: Context): Any

    operator fun plus(other: Any) = 
        BinaryOperator('+', this, Expr.of(other))

    // other operator definitions omitted here...

    companion object {
        fun of(value: Any): Expr = 
            if (value is Expr) value else Literal(value)
    }
}

```

Converting expression references in our `BlockBuilder`
accordingly, we can write the previous example as follows:

```
val myVar = Var(40)
Set(myVar, myVar + 2)
PrintLn(myVar)
```

## Control Structures

Of course a programming language can't be complete without
some sort of control structures.

So let's build a conditional expression:

```kt
class IfExpr(
    val condition: String, 
    val then: Block, 
    val otherwise: Block = Block(emptyList())
) : Expr{
    override fun eval(context: Context) {
        if (condition.evalBoolean(context)) {
            then.eval(context)
        } else {
            otherwise.eval(context)
        }
    }

    fun Else(expr: Block) = If(condition, then, expr)
}
```

And a `while` expression:

```
class WhileExpr(
    val condition: String, 
    val body: Block
) : Expr {
    override fun eval(context: Context) {
        while (condition.evalBoolean(context)) {
            body.eval(context)
        }
    }
}


And wire them up in our DSL:

```kt
class BlockBuilder {

    fun If(condition: Any, init: BlockBuilder.() -> Unit): IfExpr {
        val blockBuilder = BlockBuilder()
        init(blockBuilder)
        val ifExpr = IfExpr(Expr.of(condition), blockBuilder.build())
        statements.add(ifExpr)
        return ifExpr
    }

    fun While(condition: Any, init: BlockBuilder.() -> Unit) {
        val blockBuilder = BlockBuilder()
        init(blockBuilder)
        statements.add(WhileExpr(Expr.of(condition), blockBuilder.build())
    }
}

Now we finally have enough functionality at our disposal
to implement a "proper" simple program: FizzBuzz

```kt
val fizzBuzz = block {
    val count = Var(20)

    While (count) {
        If (count % 5) {
            If (count % 3) {
                PrintLn(count)
            }.Else {
                PrintLn("Fizz")
            }
        }.Else {
            If (count % 3) {
                PrintLn("FizzBuzz")
            }.Else {
                PrintLn("Buzz")
            }
        }
        Set(count, count - 1)
    }
}
fizzBuzz.eval(Context(1))
```

## Functions