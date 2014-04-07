package pl.vot.tomekby.Controller

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import pl.vot.tomekby.Model._

object Patient {
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
  
  lazy val registerMe = new Button("Zarejestruj się do lekarza") {
    listenTo(this)
    reactions += { case ButtonClicked(_) => CRUD.addEditAction("zarejestruj-sie-do-lekarza", "Menu główne", Main.mainMenu, this.text) }
  }
  // Wizyty pacjenta
  lazy val incomingVisits = new Button("Moje nadchodzące wizyty") {
    listenTo(this)
    reactions += {
      case ButtonClicked(_) => {
          CRUD.additionalCols = List(CRUD.cancelOwnVisit(_))
          CRUD.cancelOwnVisitColumns = List(Map("ID" -> "id"), Map("Lekarz" -> "doctor"), Map("Data" -> "date"))
          CRUD.elementsList(this.text, "zblizajace-sie-wizyty", CRUD.cancelOwnVisitColumns)
      }
    }
  }
  // Historia leczenia lekarza przez innych
  lazy val myHistory = new Button("Moja historia leczenia") {
    listenTo(this)
    reactions += {
      case ButtonClicked(_) => {
          CRUD.additionalCols = List()
          CRUD.elementsList(this.text, "historia-leczenia", List(Map("ID" -> "id"), Map("Lekarz" -> "doctor"), Map("Data" -> "date")))
      }
    }
  }
  
  // Główne menu zwyłego użyszkodnika
  lazy val mainMenu : BoxPanel = new BoxPanel(Orientation.Vertical) {
    contents += new BorderPanel { add(registerMe, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(incomingVisits, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(myHistory, BorderPanel.Position.Center) }
    // Wyjście i wylogowanie
    contents += VStrut(15);
    contents += new BorderPanel {
      add(logoutButton, BorderPanel.Position.West)
      add(exitButton, BorderPanel.Position.East)
    }
  }
}
