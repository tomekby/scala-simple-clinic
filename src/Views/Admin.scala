package Views

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import Model._
import Controller._

object Admin {
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

  // Główne menu administratora
  def mainMenu : BoxPanel = new BoxPanel(Orientation.Vertical) {
    val showUsers = new Button("Użytkownicy") {
      listenTo(this)
      val columns = List(Map("ID" -> "id"), Map("Nazwa" -> "username"), Map("Imię" -> "first_name"), Map("Nazwisko" -> "last_name"), Map("Adres e-mail" -> "email"))
      reactions += { case ButtonClicked(_) =>
          CRUD.elementsList(this.text, "zarzadzaj-uzytkownikami", columns, "uzytkownik-<id>-edytuj", "uzytkownik-<id>-usun", delExcludeIds = Set("1"))
      }
    }
    val addUser = new Button("Stwórz nowe konto użytkownika") { // Dodawanie nowego użyszkodnika
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("uzytkownik-dodaj", "Menu główne", Admin.mainMenu, this.text) }
    }
    val showGroups = new Button("Grupy") {
      listenTo(this)
      val columns = List(Map("ID" -> "id"), Map("Nazwa" -> "name"))
      reactions += {
        case ButtonClicked(_) => CRUD.elementsList(this.text, "zarzadzaj-grupami", columns, "grupa-<id>-edytuj", "grupa-<id>-usun", delExcludeIds = Set("1", "2", "3"))
      }
    }
    val addGroup = new Button("Stwórz nową grupę") { // Tworzenie nowej grupy
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("grupa-dodaj", "Menu główne", Admin.mainMenu, this.text) }
    }
    val showConfig = new Button("Opcje konfiguracyjne") {
      listenTo(this)
      val columns = List(Map("ID" -> "id"), Map("Moduł" -> "module"), Map("Opis" -> "desc"), Map("Nazwa" -> "name"))
      reactions += {
        case ButtonClicked(_) => CRUD.elementsList(this.text, "konfiguracja", columns, "konfiguracja-edytuj-<id>", "konfiguracja-usun-<id>")
      }
    }
    val addConfOption = new Button("Nowy wpis konfiguracji") { // Tworzenie nowej opcji konfiguracyjnej
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("konfiguracja-dodaj", "Menu główne", Admin.mainMenu, this.text) }
    }
    val showNewsCategories = new Button("Specjalizacje lekarskie") {
      listenTo(this)
      val columns = List(Map("ID" -> "id"), Map("Nazwa" -> "name"))
      reactions += {
        case ButtonClicked(_) => CRUD.elementsList(this.text, "zarzadzaj-specjalizacjami", columns, "specjalizacja-<id>-edytuj", "specjalizacja-<id>-usun")
      }
    }
    val addNewsCategory = new Button("Nowa specjalizacja lekarska") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("specjalizacja-dodaj", "Menu główne", Admin.mainMenu, this.text) }
    }
    val showGaleries = new Button("Wizyty u lekarzy") {
      listenTo(this)
      val columns = List(Map("ID" -> "id"), Map("Pacjent" -> "patient"), Map("Lekarz" -> "doctor"), Map("Data" -> "date"))
      reactions += {
        case ButtonClicked(_) => CRUD.elementsList(this.text, "zarzadzaj-wizytami", columns, "wizyta-<id>-edytuj", "wizyta-<id>-usun")
      }
    }
    val addGalery = new Button("Nowa wizyta") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("wizyta-dodaj", "Menu główne", Admin.mainMenu, this.text) }
    }
    val showStatics = new Button("Strony statyczne") {
      listenTo(this)
      val columns = List(Map("ID" -> "id"), Map("Tytuł" -> "title"), Map("Slug" -> "slug"))
      reactions += {
        case ButtonClicked(_) => CRUD.elementsList(this.text, "strony-statyczne", columns, "strona-edytuj-<id>", "strona-usun-<id>", delExcludeIds = Set("1"))
      }
    }
    val addStatic = new Button("Dodaj stronę statyczną") {
      listenTo(this)
      reactions += { case ButtonClicked(_) => CRUD.addEditAction("strona-dodaj", "Menu główne", Admin.mainMenu, this.text) }
    }
    // Ustawianie kontrolek
    contents += new BorderPanel { add(showUsers, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(addUser, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(showGroups, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(addGroup, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(showStatics, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(addStatic, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(showConfig, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(addConfOption, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(showNewsCategories, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(addNewsCategory, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(showGaleries, BorderPanel.Position.Center) }
    contents += VStrut(5);
    contents += new BorderPanel { add(addGalery, BorderPanel.Position.Center) }
    // Wyjście i wylogowanie
    contents += VStrut(15);
    contents += new BorderPanel {
      add(logoutButton, BorderPanel.Position.West)
      add(exitButton, BorderPanel.Position.East)
    }
  }
}
