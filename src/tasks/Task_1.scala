package tasks

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.catalyst.plans.logical.Distinct


object Task_1 {
  /** Our main function where the action happens */
  def main(args: Array[String]) {
    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val mySpark = SparkSession
    .builder()
    .appName("cereal")
    .master("local[*]")
    .getOrCreate()
    
    val dataFrame = mySpark.read.option("header", true)
    .option("inferSchema", true)
    .csv("../cereal.csv")
    
    
    println("Schema")
    println(dataFrame.printSchema())
//    dataFrame.collect().foreach(println)
    
    
    dataFrame.createOrReplaceTempView("Cereal")
    
    
    /**
     * 1st method
     */
    // least sodium cereals 
    val q1 = mySpark.sql("select name, sodium from Cereal order by sodium")
    mySpark.time(q1.show())
    println()
    
    
    //most protein cereal
    val q2 = mySpark.sql("select name, protein from Cereal where protein >= 3 order by protein desc ")
    mySpark.time(q2.show())
    println()
    
    //shownig number of mfr values
    val q3 = mySpark.sql("select count(distinct mfr) as count_mfr from cereal")
    mySpark.time(q3.show())
    println()
    
    //select cereal with small sodium amount and big protein amount ordered by rating desc
    val q4 = mySpark.sql("select name, sodium, protein, rating from cereal where sodium < 100 and protein >= 4 order by rating desc")
    mySpark.time(q4.show())
    println()
    
    //select the first best 10 cereals type c having less amount of calories
    val q5 = mySpark.sql("select name, type, calories, rating from cereal where type == \"C\" AND calories <= 100 order by rating desc limit 10")
    mySpark.time(q5.show())
    println()
    
    
    /**
     * 2nd method
     */
    // least sodium cereals 
    val q1_ = dataFrame.select("name", "sodium")
                       .orderBy("sodium")
                       .limit(5)
    mySpark.time(q1_.show())
    println()
    
    //most protein cereal
    val q2_ = dataFrame.select("name", "protein")
                       .where("protein >= 3")
                       .orderBy(col("protein").desc)
    mySpark.time(q2_.show())
    println()
    
    //shownig number of mfr values
    val q3_ = dataFrame.select(countDistinct("mfr"))
    mySpark.time(q3_.show())
    println()
    
    //select cereal with small sodium amount and big protein amount ordered by rating desc
    val q4_ = dataFrame.select("name", "sodium", "protein", "rating")
                       .where("sodium < 100 and protein >= 4")
                       .orderBy(desc("rating"))
    mySpark.time(q4_.show())
    println()
    
    //select the first best 10 cereals type c having less amount of calories
    val q5_ = dataFrame.select("name", "type", "calories", "rating")
                       .where("type == \"C\" AND calories <= 100")
                       .orderBy(desc("rating"))limit(10)
    mySpark.time(q5_.show())
    println()
    
    
    /**
     * Queries for graphs
     */
    //number of cereals with different amount of sodium
    val qg1 = mySpark.sql("select sodium, count(*) as num_of_cereals from Cereal group by sodium order by sodium desc")
    mySpark.time(qg1.show())
    println()
    
    //number of cereals with different amount of vitamins
    val qg2 = mySpark.sql("select vitamins, count(*) as num_of_cereals from Cereal group by vitamins order by vitamins desc")
    mySpark.time(qg2.show())
    println()
    
    val qg3 = mySpark.sql("select mfr, count(*), round(count(*) * 100 / sum(count(*)) over (), 2) as percentage " +
                          "from cereal group by mfr order by percentage desc")
    mySpark.time(qg3.show())
    println()
    
    val qg4 = mySpark.sql("select type, count(*), round(count(*) * 100 / sum(count(*)) over (), 2) as percentage " +
                          "from cereal group by type order by percentage desc")
    mySpark.time(qg4.show())
    println()
    
    val qg5 = mySpark.sql("select shelf, count(*) as number_of_products from cereal group by shelf order by shelf")
    mySpark.time(qg5.show())
    println()
  }
}