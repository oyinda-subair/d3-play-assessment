package database

object SlickCodeGen {
  def main(args: Array[String]): Unit = {
    slick.codegen.SourceCodeGenerator.main(
      Array("slick.jdbc.PostgresProfile",
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/playdb",
        "/Users/oyindamolasubair/Workspace/d3-play-assessment/app/database",
        "postgres",
        "postgres",
        "")
    )
  }
}
