Product-Extractor
=================

To compile and run this program, you will need the [Jsoup library](http://jsoup.org/).

### Compilation
```shell
javac -cp jsoup-*.jar Department.java Product.java
javac -cp .:jsoup-*.jar DepartmentSpider.java
javac -cp .:jsoup-*.jar TescoGroceries.java
```

### Running the Spider
```shell
java -cp .:jsoup-*.jar TescoGroceries
```
