_This is work in progress!_

# How Far can we Take Kotlin DSLs? Yes!

Is it possible to build a full "inner" programming language as a
Kotlin DSL? For science, we have to find out! 



## Expressions

Before we start our DSL, let's quickly build up expressions as a building block.


Let's assume our languages uses an interface `Expr` to represent expressions that we can evaluate in some context that we'll come back to later: 

```
class Context

interface Expr {
    fun eval(context: Context): Any
}
```

### Literals

For literals, we can define a class `Const` as follows:

```
class Const(val value: Any) : Expr {
    override fun eval(context: Context) = value
}
```


### Trees 

Operator overloading allows us to recursively build an 
expression tree with regular operators and operator precedence.

First, we define a binary expression node that is able
to do some math:

```
class BinaryOperator(
    val op: String, 
    val leftExpr: Expr, 
    val rightExpr: Expr
): Expr {
    fun eval(context: Context) {
        val l = leftExpr.eval(context) as Double
        val r = rightExpr.eval(context) as Double
        return when (op) {
            "+" -> l + r
            "*" -> l * r
            // ...
            else -> throw UnsupportedOperationException(op)
        }
    }
}
```

Then we use operator overloading to enable building expression trees with the corresponding operators.

```
operator fun Expr.add(other: Expr) = 
    BinaryOperator("+", this, other)

operator fun Expr.mul(other: Expr) = 
    BinaryOperator("-", this, other) 
...
```

Now we can build an expression that adds two numbers with the following
code snippet:

```
val addExpr = Const(40) + Const(2)
````

We can test if this works as expected by evaluating the expression with the following line, which should print 42:
```
print(addExpr.eval(Context()))
```

Of course there is a simpler way to calculate the sum of
40 and 2. The important difference here is that we have generated an expression tree that we can inspect and manipulate and where we can repeat the included operations if we want to.

For instance, after adding variables to our expressions, we could implement symbolic derivation. 


## Blocks 

With basic expressions in place,
we can start working on our "actual" DSL.

First, we define a class `Block` that bundles multiple expressions and exectutes
them sequentially:

```
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


```
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

```
fun block(init): BlockExpr {
    val builder = BlockBuilder()
    builder.init()
}
```

Now we can use what we have defined so far and create our first block of code, which should just print "42" again, with the "print"
command moved to our inner language now.

```
  val b = block {
    PrintLn(Const(40) + Const(2))
  }

  b.eval(Context())
```




### Variables



For variables, we expand our code builder to (just) keep track of the number
of variables declared so far -- and add :




```
fun Var(initialValue: Expr): VarExpr {
    // Add a variable to the currnet builder context and 
    // return a reference expression
)
```




## Control Structures


```kt
fun If (val expr: String, )
```
