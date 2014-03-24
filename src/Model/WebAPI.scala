/**
 * Obsługa strony via WebAPI
 */
package Model

import java.net.URI
import java.net.URL
import uk.co.bigbeeconsultants.http.HttpBrowser
import uk.co.bigbeeconsultants.http.request.RequestBody
import org.json4s._
import org.json4s.native.JsonMethods._

// Singleton aby nie było akcji typu 300 logowań podczas jednego odpalenia aplikacji
object WebAPI {
  // Domena strony obsługiwanej przez WebAPI
  var domain : String = ""
  // Klasa odpowiedzialna za obsługę requestów
  private val browser = new HttpBrowser
  var loggedIn = false
  // Podstawowe funkcje API
  private def strToUrl(uri : String) : URL = URI.create(domain+uri+(if((uri indexOf '?') != -1) "&api=true" else "?api=true")).toURL
  def login(username : String, pwd : String) : Boolean = {
    val loginData = RequestBody(Map("username" -> username, "password" -> pwd))
    loggedIn = browser.post(strToUrl("api/login"), Some(loginData)).status.code == 200
    loggedIn
  }
  def logout() : Unit = if(loggedIn) loggedIn = browser.head(strToUrl("api/logout")).status.code != 200
  def getToken() : String = browser.get(strToUrl("api/get-token")).body.asString
  def isInGroup(group : String) : Boolean = browser.get(strToUrl("api/group/"+group)).body.asString == "TRUE"
  def getUsername(login : Boolean = false) : String = browser.get(strToUrl("api/username"+(if(login) "/1" else ""))).body.asString

  // CRUD
  def read(uri : String) : List[Map[String, String]] = {
    implicit val formats = DefaultFormats
    val data = browser.get(strToUrl(uri))
    if(data.status.code != 200) List[Map[String, String]]()
    else parse(data.body.asString).extract[ List[Map[String, String]] ]
  }
  def add(uri : String, data : Map[String, String]) : Boolean = {
    val newData = RequestBody(data ++ Map("csrf" -> getToken))
    browser.post(strToUrl(uri), Some(newData)).status.code == 200
  }
  def editForms(uri : String) : Map[String, Map[String, String]] = {
    implicit val formats = DefaultFormats
    val data = browser.get(strToUrl(uri))
    if(data.status.code != 200) Map[String, Map[String, String]]()
    else parse(data.body.asString).extract[ Map[String, Map[String, String]] ]
  }
  def edit(uri : String, data : Map[String, String]) : Boolean = {
    val forms = editForms(uri)
    val oldData : Map[String, String] = forms.foldLeft(Map[String, String]()) ((all, curr) => all ++ Map(curr._1 -> curr._2("data").toString))
    val newData = RequestBody(oldData ++ data ++ Map("csrf" -> getToken))
    browser.post(strToUrl(uri), Some(newData)).status.code == 200
  }
  def delete(uri : String) : Boolean = {
    browser.get(strToUrl(uri+"?csrf="+getToken)).status.code == 200
  }
}
