package models

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import javax.inject._

import models.Schema.users
import play.api.Play


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}


case class User(id: Long, name: String)
case class UserFormData(name: String)

object Schema {

  import slick.lifted.{Tag, TableQuery}

  class Users(tag: Tag) extends Table[User](tag, "USER"){
    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    def name = column[String]("NAME")
    override def * = (id, name) <>(User.tupled, User.unapply)
  }
  val users = TableQuery[Users]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def add(user: User): Future[String] = {
    dbConfig.db.run(users += user).map(result => "User added").recover{
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(users.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[User]] = {
    dbConfig.db.run(users.result)
  }

  def updateName(id: Long, newName: String): Future[String] = {
    dbConfig.db.run(users.filter(_.id === id).map(_.name).update(newName)).map(result => "User added").recover{
      case ex: Exception => ex.getCause.getMessage
    }
  }





}
