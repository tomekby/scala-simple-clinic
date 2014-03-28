package Controller

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import Model._

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
  
  // Główne menu zwyłego użyszkodnika
  lazy val mainMenu : BoxPanel = new BoxPanel(Orientation.Vertical) {
    val registerMe = new Button("Zarejestruj się do lekarza") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("zarejestruj-sie-do-lekarza", "Menu główne", Patient.mainMenu, this.text) }
    }
    val incomingVisits = new Button("Nadchodzące wizyty") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => println("Tu będą wyświetlane nadchodzące wizyty pacjenta") }
    }
    val myHistory = new Button("Moja historia leczenia") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => println("Historia leczenia pacjenta") }
    }
    val editAccount = new Button("Edytuj konto") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => println("Edycja własnego konta") }
    }
    contents += new BorderPanel { add(registerMe, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(incomingVisits, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(myHistory, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(editAccount, BorderPanel.Position.Center) }
    // Wyjście i wylogowanie
    contents += VStrut(15);
    contents += new BorderPanel {
      add(logoutButton, BorderPanel.Position.West)
      add(exitButton, BorderPanel.Position.East)
    }
  }
}
