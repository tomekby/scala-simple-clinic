/**
 * MAIN TODOS:
 * !TODO ekrany dla użyszkodnika, lekarza i recepcji
 * !TODO dodanie osobie z rejestracji możliwości tworzenia nowych użyszkodników
 * USABILITY/LAYOUT:
 * !TODO ustawienie kontrolek na górze w prawym panelu albo coś innego; anyway wykombinować coś
 * !TODO ograniczenie maksymalnej szerokości kolumn przy liście elementów
 * !TODO zastanowić się nad fixem kolejności formularzy dodawania/edycji (np. lista map dwuwymiarowych?)
 * !TODO podczas rejestracji automatyczne ustalanie godziny po wybraniu dnia (?)
 * !TODO może jakiś start screen jako strona główna po zalogowaniu?
 * !TODO ograniczenie ilości requestów do minimum...
 * WALIDACJA
 * !TODO podczas rejestracji sprawdzanie, czy lekarz i pacjent to nie jest ta sama osoba
 * !TODO przy zmianie dni/godzin pracy lekarza walidacja tego
 */
/**
 * 
 * PREZENTACJA NA SOiSK NA PRZYSZŁY TYDZIEŃ
 * ORM, COŚ INNEGO, NP. KOMERCYJNE WYKORZYSTANIE BAZ OR STH
 * 
 */
package Controller

import scala.swing.BoxPanel
import swing._
import Swing._
import Model.WebAPI
import event._
import GridBagPanel._
import Views._
import Model._

object Main extends SimpleSwingApplication {

  WebAPI.domain = "http://tomekby.vot.pl/psio2014/"

  def top = new MainFrame {
    // Dane okienka
    title = "Rejestracja do lekatrza etc."
    resizable = false
    // Potrzebne do custom close'a
    import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
    peer.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    /**
     * Inicjalizacja widoków
     */
    // Inicjalizacja widoku admina, usera, doktora, recepcji, logowania
    Admin.initialize(exitButton, logoutButton)
    Reception.initialize(exitButton, logoutButton)
    Doctor.initialize(exitButton, logoutButton)
    Patient.initialize(exitButton, logoutButton)
    LoginForm.initialize(exitButton, logoutButton, repaintWith, frameContent)

    // Tworzenie menu
    menuBar = new MenuBar {
      contents += new Menu("Program") {
        mnemonic = Key.Alt
        contents += new MenuItem(Action("O programie i twórcy") {
            println("huehuehue okienka")
        }) { mnemonic = Key.O }
        contents += new MenuItem(Action("O programie i twórcy - Java™ version") {
            println("huehuehue okienka Java this time")
        }) { mnemonic = Key.J }
        contents += new MenuItem(Action("Wyjście") {
            WebAPI.logout
            quit
        }) { mnemonic = Key.W }
      }
    }

    /**
     * Repaint okienka (podmiana kontrolek na nowe)
     */
    def repaintWith(controls : BoxPanel, pageName : String = "") : Unit = {
      top.resizable = true
      frameContent.contents.clear
      allContent.contents.clear
      // Jesli uzytkownik jest zalogowany info o tym jako kto etc.
      if(WebAPI.loggedIn) {
        if(pageName != "Menu główne") allContent.contents += mainMenu
        frameContent.contents += new BorderPanel {
          add(new BoxPanel(Orientation.Vertical) {
            contents += loggedInAs
            contents += new Label("Jesteś tutaj: "+pageName) {
                horizontalAlignment=Alignment.Left
                verticalAlignment=Alignment.Top
            }
          }, BorderPanel.Position.North)
        }
      }
      frameContent.contents += controls // Dopisanie kontrolek
      allContent.contents += frameContent
      // Magia mająca na celu odświeżenie i resize okienka do odpowiednich rozmiarów
      allContent.revalidate
      if(WebAPI.loggedIn) peer.setSize(allContent.preferredSize.width + 20, allContent.preferredSize.height + 70)
      else peer.setSize(allContent.preferredSize.width, allContent.preferredSize.height + 50)
      peer.revalidate
      top.resizable = false
    }
    
    /**
     * Podstawowe kontrolki
     */
    // Wybór głównego menu
    def mainMenu = {
      if(WebAPI.isInGroup("Administrator")) Admin.mainMenu
      else if(WebAPI.isInGroup("Lekarz")) Doctor.mainMenu
      else if(WebAPI.isInGroup("Recepcja")) Reception.mainMenu
      else Patient.mainMenu
    }
    // Wyjście z programu
    def exitButton = new Button("Wyjście") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => {
        WebAPI.logout
        quit
      }}
    }
    // Wylogowanie
    def logoutButton = new Button("Wyloguj") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => {
        WebAPI.logout
        repaintWith(LoginForm.get)
      }}
    }
    // Zalogowany jako kto?
    def loggedInAs = new Label("Zalogowany jako: "+WebAPI.getUsername()) {
      verticalAlignment=Alignment.Top
      horizontalAlignment=Alignment.Left
    }

    // Główna zawartość okienka
    lazy val frameContent = new BoxPanel(Orientation.Vertical) {
      contents += LoginForm.get
      border = Swing.EmptyBorder(5, 5, 15, 5)
    }
    // Zawartość całego okna (w tym menu)
    lazy val allContent = new BoxPanel(Orientation.Horizontal) {
      contents += frameContent
      border = Swing.EmptyBorder(5, 5, 15, 5)
    }

    contents = allContent
    centerOnScreen

    // Wylogowanie przy zamykaniu okienka
    override def closeOperation() { 
      WebAPI.logout
      sys.exit(0)
    }
  }
}
