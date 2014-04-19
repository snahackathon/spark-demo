//Value
val a: Int = 10
val b = 20

//Function
def add(x: Int, y: Int) = x + y
add(a, b)

//Collection
val seq = Seq(10, 20, 30, 40, 50)
//Collection access
seq(0)
seq(1)

//High-order functions
seq.reduce(add)
seq.map(x => x * x).reduce(add)
seq.filter(x => x > 25).reduce(add)
