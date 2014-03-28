package Controller

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import Model._

object Doctor {
  private var exitButton : Button = new Button
  private var logoutButton : Button = new Button
  private var initialized = false

  def initialize(exit : Button, logout : Button) = {
    if( ! initialized) {
      exitButton = exit
      logoutButton = logout
      initialized = true
    }
  }
  
  lazy val mainMenu : BoxPanel = new BoxPanel(Orientation.Vertical) {
    val registerMe = new Button("Zarejestruj się do lekarza") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("zarejestruj-sie-do-lekarza", "Menu główne", Doctor.mainMenu, this.text) }
    }
    val registerSomeone = new Button("Zarejestruj pacjenta do lekarza") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("zarejestruj-do-lekarza", "Menu główne", Doctor.mainMenu, this.text) }
    }
    val todayVisits = new Button("Pacjenci zarejestrowani na dziś") {
      listenTo(this)
      reactions += {
        case ButtonClicked(_) => {
            CRUD.additionalCols = List(
              CRUD.cancelVisit(_)
            )
            CRUD.cancelVisitColumns = List(Map("ID" -> "id"), Map("Pacjent" -> "patient"), Map("Data" -> "date"))
            CRUD.elementsList(this.text, "dzisiejsze-wizyty", CRUD.cancelVisitColumns)
        }
      }
    }
    val incomingVisits = new Button("Moje nadchodzące wizyty") {
      listenTo(this)
      reactions += {
        case ButtonClicked(_) => {
            CRUD.additionalCols = List(CRUD.cancelOwnVisit(_))
            CRUD.cancelOwnVisitColumns = List(Map("ID" -> "id"), Map("Lekarz" -> "doctor"), Map("Data" -> "date"))
            CRUD.elementsList(this.text, "zblizajace-sie-wizyty", CRUD.cancelOwnVisitColumns)
        }
      }
    }
    val myHistory = new Button("Moja historia leczenia") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => println("Tu będzie historia leczenia lekarza") }
    }
    contents += new BorderPanel { add(registerMe, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(registerSomeone, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(todayVisits, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(incomingVisits, BorderPanel.Position.Center) }
    // Wyjście i wylogowanie
    contents += VStrut(15);
    contents += new BorderPanel {
      add(logoutButton, BorderPanel.Position.West)
      add(exitButton, BorderPanel.Position.East)
    }
  }
}
