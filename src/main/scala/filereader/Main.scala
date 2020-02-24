package filereader

import java.io.File
import java.time.Instant

import org.mongodb.scala._
import org.mongodb.scala.bson.BsonDateTime

final case class FileInfo(name: String, extension: String, date: BsonDateTime, path: String)

object Main {

  def main(args: Array[String]): Unit = {

    //set directory to search as project root
    val hdir = new File(".").getAbsoluteFile
    val a = dirData(hdir)
    dirData(hdir).foreach(uploadData)
  }


  //Get all file data using recursion to check for sub directories
  def dirData(hdir: File): Stream[FileInfo]={
    if (hdir.isDirectory){
     hdir.listFiles.flatMap(dirData).toStream
    }
    else if (hdir.isFile){
      Stream(fileData(hdir))
    }
    else
      Stream.empty
   }

  //Store file data into a case class for uploading of file data to MongoDB
  def fileData(file: File): FileInfo ={
  FileInfo(file.getName, fileExt(file.getName), BsonDateTime(file.lastModified),file.getCanonicalPath)
  }

  def lastModDate(ms :Long): Instant ={
    val date = Instant.ofEpochMilli(ms)
    date
  }

  //get file extension using RegEx
  def fileExt(s : String): String = {
    val fileNameRx = """.*\.([^.]+)""".r
    s match {
    case fileNameRx(ext) => ext
    case _ =>"No Extension"
    }
  }

  //upload file information to MongoDB using mongo scala driver
  def uploadData(fileinfo : FileInfo): Unit={
    val mongoClient: MongoClient = MongoClient()
    val database: MongoDatabase = mongoClient.getDatabase("file-reader")
    val collection: MongoCollection[Document] = database.getCollection("file-data")

    val document: Document = Document("FileName" -> fileinfo.name, "FileExtension" -> fileinfo.extension, "Last Modified" -> fileinfo.date, "File Path" -> fileinfo.path)
    val insertObservable: Observable[Completed] = collection.insertOne(document)

     insertObservable.subscribe(new Observer[Completed] {
       override def onNext(result: Completed): Unit = println("Inserted")
       override def onError(e: Throwable): Unit = println("Failed")
       override def onComplete(): Unit = println("Completed")
     })
  }
}
