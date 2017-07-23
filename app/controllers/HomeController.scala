package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import models._
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.duration._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
object UserForm{
  val form = Form(
    mapping(
      "name" -> nonEmptyText
    )(UserFormData.apply)(UserFormData.unapply)
  )
}


@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def listAllUsers = Action.async{ implicit request =>
    Schema.listAll map { users =>
      Ok(views.html.listAll(users))
    }
  }

  def inputForm = Action { implicit request =>
    Ok(views.html.input(UserForm.form))
  }

  def add = Action.async { implicit request =>
    UserForm.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok(views.html.input(errorForm))),
      data => {
        val newUser = User(0, data.name)
        Schema.add(newUser).map(result =>
          Redirect(routes.HomeController.listAllUsers))
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    Schema.delete(id).map { result =>
      Redirect(routes.HomeController.listAllUsers)
    }
  }

  def details(id: Long) = Action.async{ implicit request =>
    Schema.get(id).map{u =>
      Ok(views.html.details(u.get, UserForm.form))
    }
  }



  def update(id: Long) = Action.async { implicit request =>

    val user = Await.result(Schema.get(id).map(result => result.get), 5 seconds)

    UserForm.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok(views.html.details(user, errorForm))),
      data => {

        Schema.updateName(user.id, data.name).map(result =>
          Redirect(routes.HomeController.listAllUsers))
      }
    )
  }


    /* Schema.updateName(id, newName)
     Redirect(routes.HomeController.details(id))
     */




















}
