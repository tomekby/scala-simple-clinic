package pl.vot.tomekby.Controller

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import pl.vot.tomekby.Model._

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
  
  lazy val registerSomeone = new Button("Zarejestruj pacjenta do lekarza") {
    listenTo(this)
    reactions += { case ButtonClicked(_) => CRUD.addEditAction("zarejestruj-do-lekarza", "Menu główne", Main.mainMenu, this.text) }
  }

  lazy val mainMenu : BoxPanel = new BoxPanel(Orientation.Vertical) {
    // Pacjenci zarejestrowani do lekarza
    val todayVisits = new Button("Pacjenci zarejestrowani na dziś") {
      listenTo(this)
      reactions += {
        case ButtonClicked(_) => {
            CRUD.additionalCols = List(
              CRUD.cancelVisit(_),
              CRUD.patientHistory(_),
              CRUD.editVisit(_)
            )
            CRUD.cancelVisitColumns = List(Map("ID" -> "id"), Map("Pacjent" -> "patient"), Map("Data" -> "date"))
            CRUD.elementsList(this.text, "dzisiejsze-wizyty", CRUD.cancelVisitColumns)
        }
      }
    }
    contents += new BorderPanel { add(Patient.registerMe, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(registerSomeone, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(todayVisits, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(Patient.incomingVisits, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(Patient.myHistory, BorderPanel.Position.Center) }
    // Wyjście i wylogowanie
    contents += VStrut(15);
    contents += new BorderPanel {
      add(logoutButton, BorderPanel.Position.West)
      add(exitButton, BorderPanel.Position.East)
    }
  }
}
