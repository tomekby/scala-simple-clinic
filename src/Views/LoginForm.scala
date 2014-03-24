package Views

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import Model._
import Controller._

object LoginForm {
  private var exitButton : Button = new Button
  private var logoutButton : Button = new Button
  private var repaintFunc : (BoxPanel, String) => Unit = (BoxPanel, String) => Unit
  private var frameContent : BoxPanel = new BoxPanel(Orientation.NoOrientation)
  private var initialized = false

  def initialize(exit : Button, logout : Button, repaint : (BoxPanel, String) => Unit, content : BoxPanel) = {
    if( ! initialized) {
      exitButton = exit
      logoutButton = logout
      repaintFunc = repaint
      frameContent = content
      initialized = true
    }
  }

  def get = new BoxPanel(Orientation.Vertical) {
    lazy val username = new TextField("")
    lazy val password = new PasswordField("")
    contents += new Label("Użytkownik:")
    contents += VStrut(2)
    contents += username
    contents += VStrut(5)
    contents += new Label("Hasło:")
    contents += VStrut(2)
    contents += password
    contents += VStrut(10)
    contents += new Button("Zaloguj się do systemu") {
      listenTo(this, password.keys, username.keys)
      def login : Unit = { // Próba zalogowania
        if(WebAPI.login(username.text, password.password.mkString)) {
          var menu = new BoxPanel(Orientation.NoOrientation)
          if(WebAPI.isInGroup("Administrator")) menu = Admin.mainMenu // Logowanie admina
          else if(WebAPI.isInGroup("Recepcja")) menu = Reception.mainMenu // Logowanie recepcji
          else if(WebAPI.isInGroup("Lekarz")) menu = Doctor.mainMenu // Logowanie lekarza
          else menu = Patient.mainMenu // Logowanie pacjenta
          // Czyszczenie danych logowania
          username.text = ""
          password.peer.setText("")
          // Inicjalizacja obsługi CRUD'a i repaint okno
          repaintFunc(menu, "Menu główne")
          CRUD.initialize(repaintFunc, menu, frameContent)
        }
        else {
          Dialog.showMessage(this, "Złe dane logowania!", "Błąd")
          password.peer.setText("")
        }
      }
      reactions += {
        case ButtonClicked(_) => login
        case e: KeyPressed => if(e.key.equals(Key.Enter)) login
      }
    }
    border = Swing.EmptyBorder(15, 15, 15, 15)
  }
}
