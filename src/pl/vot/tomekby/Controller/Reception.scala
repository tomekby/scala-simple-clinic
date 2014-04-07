package pl.vot.tomekby.Controller

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import pl.vot.tomekby.Model._

object Reception {
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
    contents += new BorderPanel { add(Patient.registerMe, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(Patient.incomingVisits, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(Patient.myHistory, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(Doctor.registerSomeone, BorderPanel.Position.Center) }
    // Wyjście i wylogowanie
    contents += VStrut(15);
    contents += new BorderPanel {
      add(logoutButton, BorderPanel.Position.West)
      add(exitButton, BorderPanel.Position.East)
    }
  }
}
