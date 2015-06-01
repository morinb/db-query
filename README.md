# DB-Query

DB-Query is a small framework that help to map SQL ResultSet into scala object.

All you need to do is create a class with all its parameters as Option[T].
You will also need to create an apply method in the companion object to ease the conversion :

```scala
    class MyTable(val id: Option[Int], val name: Option[String], val creationDate: Option[Date])

    object MyTable {
        import org.bm.dbquery.Implicits._
        def apply(implicit resultSet: ResultSet): MyTable = new MyTable(column("id"), column("name"), column("creation_date"))
    }
```    

the implicit ResultSet is needed to ease the writing. Typically, the column method takes more than a column name.
Here is its full signature :

```scala
    def column[T](columnName: String)(implicit f: (ResultSet, String) => T, rs: ResultSet, columnNames: IndexedSeq[String]): Option[T]
``` 

The ```f``` function is the real mapper it takes a ResultSet and a column name to produce a ```T``` object.
The ```columnNames``` sequence is usually extracted from the ```ResultSet``` metadata, but you can give your own sequence if you already have your column names available. 

The org.bm.dbquery package contains an object that defines usefull implicits : [org.bm.dbquery.Implicits](https://github.com/morinb/db-query/blob/master/src/main/scala/org/bm/dbquery/package.scala#L37)
It contains a columnNames method that takes an implicit ResultSet, hence the implicit ResultSet of the MyTable apply method.
It contains also a bunch of implicit method that map the ResultSet.getXXX methods.

# Utils classes
There are actually 2 utils classes : WithResource and ResultSetDumper.
 
## WithResource

WithResource define a method that ease the handling of closeable resource, like SQL connection for example.

```scala
    import org.bm.dbquery.utils.WithResource._

    val conn: Connection = ...


    val result = withResource(conn) {
        // do something with the connection and return a result
        // the connection will be closed at the end of the block and the result returned.
        // if an exception occurs it is thrown. If an additional exception is thrown while closing the resource, 
        // it is added as suppressed exception. 
    }
```

## ResultSetDumper

ResultSetdumper is an utility class that define two methods : ```dump``` and ```format```
The ```dump``` method maps a ResultSet into a List[List[String]], thus you can access your data in a more programmatic way :

```scala
    val rs: ResultSet = ...
    val results = ResultSetDumper.dump(rs)
    
    println(results(0)(0)) // the first column of the first row

```

It uses a tail recursive accumulator method to gather data from the ResultSet

The ```format``` method takes a List[List[String]] and format the data into one String that represents the data in a tabular way.
With "|" character to delimits the columns and "-" for the separation between column names and the datas.