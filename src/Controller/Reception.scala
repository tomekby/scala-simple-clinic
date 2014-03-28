package Controller

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import Model._

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
    // Menu recepcji
  }
}
