package Controller

import scala.swing.BoxPanel
import swing._
import Swing._
import event._
import GridBagPanel._
import Model._

object CRUD {
  // Funkcja odpowiedzialna za repainting głównego okienka
  private var repaintFunc : (BoxPanel, String) => Unit = (BoxPanel, String) => Nil
  // Pobieranie menu głównego
  private var mainMenu     = new BoxPanel(Orientation.NoOrientation)
  // Zawartość frame'a
  private var frameContent = new BoxPanel(Orientation.NoOrientation)
  private var initialized = false

  def initialize(repaint : (BoxPanel, String) => Unit, menu : BoxPanel, content : BoxPanel) {
    if( ! initialized) {
      frameContent = content
      repaintFunc = repaint
      mainMenu = menu
      initialized = true
    }
  }

  // Wielkość kontrolek dodawania/edycji
  val labelWidth = 150
  val controlWidth = 450
  // Tworzenie labela dla kontrolki
  def addEditLabel(text : String) : BorderPanel = new BorderPanel {
    add(new Label(text+":"), BorderPanel.Position.East)
    border = Swing.EmptyBorder(5, 5, 5, 10)
    preferredSize = new Dimension(labelWidth, 25)
    maximumSize = preferredSize 
    minimumSize = preferredSize
  }
  // Przetwarzanie kolejnych atrybutów dla kontrolki tekstowej
  // Swoją drogą niby wszystko dziedziczy po Control, ale to badziewie jest zdefiniowane jako abstract... polimorfizm ograniczony, dla różnych kontrolek inaczej :(
  def textAttribs(field : TextComponent, attribs : List[String]) : Unit = attribs.foreach{attrib => {
      // Nazwa i wartość atrybutu
      if(attrib.indexOf(':') != -1) {
        val head = attrib.substring(0, attrib.indexOf(':'))
        val end = attrib.substring(attrib.indexOf(':')+1, attrib.length)
        if(head == "disabled" && (end == "true" || end == "disabled")) field.editable = false
        else if(head == "required" && end == "1") requiredFields = requiredFields ::: List(field.name)
        else if(head == "placeholder") field.tooltip = end
      }
  }}

  /**
   * Róże kontrolki
   */
  def addEditTextField(text : String, fieldName : String, attribs : List[String] = List()) : TextField = new TextField(text) {
    preferredSize = new Dimension(controlWidth, 25)
    minimumSize = preferredSize
    name = fieldName
    textAttribs(this, attribs)
  }
  def addEditTextArea(text : String, fieldName : String, attribs : List[String] = List()) : TextArea = new TextArea(text) {
    wordWrap = false
    name = fieldName
    textAttribs(this, attribs)
  }
  def addEditTextAreaPane(control : TextComponent, attribs : List[String] = List()) : ScrollPane = new ScrollPane(control) {
    preferredSize = new Dimension(controlWidth, 300);
    minimumSize = preferredSize
  }
  def addEditPasswordField(fieldName : String, attribs : List[String] = List()) : PasswordField = new PasswordField {
    preferredSize = new Dimension(controlWidth, 25)
    minimumSize = preferredSize
    name = fieldName
    textAttribs(this, attribs)
  }
  def comboAttribs(field : ComboBox[String], attribs : List[String]) : Unit = attribs.foreach{attrib => {
      val curr = attrib.split("\\:").toList
      if(curr.head == "disabled" && (curr.last == "true" || curr.last == "disabled")) field.enabled = false
      else if(curr.head == "required" && curr.last == "1") requiredFields = requiredFields ::: List(field.name)
      else if(curr.head == "selected") field.selection.item = comboValuesMap(field.name).foldLeft("")((a, c) => if(c._2 == curr.last) c._1 else a)
  }}
  def addEditComboBox(fieldName : String, items : List[String], attribs : List[String] = List()) : ComboBox[String] = new ComboBox(items) {
    preferredSize = new Dimension(controlWidth, 25)
    minimumSize = preferredSize
    name = fieldName
    comboAttribs(this, attribs)
  }

  /**
   * Tworzenie formularza dodawania/edycji czegokolwiek
   */
  var requiredFields : List[String]           = List() // Nazwy wymaganych pól
  var textControls   : List[TextComponent]    = List() // Kontrolki tekstowe (TextField/TextArea)
  var passwdControls : List[PasswordField]    = List() // Hasła... Szkoda trochę ograniczeń polimorfizmu... :(
  var comboControls  : List[ComboBox[String]] = List() // Lista ComboBoxów
  var comboValuesMap : Map[String, Map[String, String]] = Map()  // Wartości wysłane do serwera jeśli wybierzemy coś z comboboxa

  /**
   * Jakie to szczęście, że Scala obsługuje funkcje wyższego rzędu :)
   * @param uri URI do strony z której będziemy brali dodawanie/edycję
   * @param repaintFunc funkcja robiąca repaint okienka
   * @param repaintControls kontrolki do których będzie repaint jeśli wszystko OK
   * @param repaintName nazwa strony do której będzie powrotny 
   */
  def getForms(uri : String, repaintControls : BoxPanel, repaintName : String) : BoxPanel = {
    // Tworzenie formularza
    if(WebAPI.loggedIn) {
      lazy val forms = WebAPI.editForms(uri)
      new BoxPanel(Orientation.Vertical) {
        // Przetwarzanie kolejnych formularzy
        forms.foreach{case(fieldName, value) => {
          val attribs = (value("attr") split "\\|").toList // Atrybuty tego pola (wymagane/disabled or sth); BTW lovely regex split...
          contents += new BoxPanel(Orientation.Horizontal) {
            contents += addEditLabel(value("desc"))
            // Dopisanie kontrolek do listy z tego formularza
            if( ! value.contains("type") || value("type") == "input") textControls = textControls ::: List(addEditTextField(value("data"), fieldName, attribs))
            else if(value("type") == "textarea") {
              textControls = textControls ::: List(addEditTextArea(value("data"), fieldName, attribs))
              contents += addEditTextAreaPane(textControls.last)
            } else if(value("type") == "password") {
              passwdControls = passwdControls ::: List(addEditPasswordField(fieldName, attribs))
              contents += passwdControls.last
            } else if(value("type") == "select") {
              val data = value("data") split "\\|" // Wszystkie możliwości
              var values : List[String] = List() // Wartości dla combo
              data.foreach{v =>
                val tuple = (v split "\\:").toList // Pobieranie wartości wyświetlanej
                values = values ::: List(if(tuple.last == "0") "" else tuple.last)
                var inserted : Map[String, String] = Map()
                if(comboValuesMap.exists(_._1 == fieldName))
                  inserted = comboValuesMap(fieldName) ++ Map((if(tuple.last == "0") "" else tuple.last) -> tuple.head)
                else inserted = Map((if(tuple.last == "0") "" else tuple.last) -> tuple.head)
                comboValuesMap = comboValuesMap ++ Map(fieldName -> inserted)
              }
              comboControls = comboControls ::: List(addEditComboBox(fieldName, values, attribs))
              contents += comboControls.last
            }
            // Dodanie do listy kontrolek
            if( ! value.contains("type") || value("type") == "input") contents += textControls.last
          }
        }}

        // Robimy content okienka
        contents += new BorderPanel { 
          add(new Button("Zapisz") {// Reakcje na eventy
            listenTo(this)
            reactions += {
              case ButtonClicked(_) => {
                var data : Map[String, String] = Map() // Dodawanie danych
                textControls.foreach{x => data = data ++ Map(x.name -> x.text)} // Zbieranie danych z kontrolek tekstowych
                passwdControls.foreach{x => data = data ++ Map(x.name -> x.password.mkString)} // Zbieranie danych z hasłami
                comboControls.foreach{x => data = data ++ Map(x.name -> comboValuesMap(x.name)(x.selection.item))} // Zbieranie danych z comboboxów
                // Sprawdzenie, czy wymagane pola są wypełnione
                // Lets make it other way... functional way!
                var requiredOk = textControls.foldLeft(true)((all, curr) => // Sprawdzenie czy pola tekstowe są wypełnione jeśli trzeba
                    all && ( ! requiredFields.exists(_ == curr.name) || (requiredFields.exists(_ == curr.name) && ! (curr.text == "")))
                )
                requiredOk &&= passwdControls.foldLeft(true)((all, curr) => // Sprawdzenie czy pola z hasłami są wypełnione jeśli trzeba
                    all && ( ! requiredFields.exists(_ == curr.name) || (requiredFields.exists(_ == curr.name) && ! (curr.password.mkString == "")))
                )
                if(WebAPI.edit(uri, data)) { // Próba zapisania
                  repaintFunc(repaintControls, repaintName)
                  // Czyszczenie kontrolek
                  textControls = List(); passwdControls = List()
                  comboControls = List(); comboValuesMap = Map()
                  requiredFields = List()
                }
                else Dialog.showMessage(frameContent, "Wpisałeś błędne dane!", "Błąd!")
              }
            }
          }, BorderPanel.Position.Center)
          border = Swing.EmptyBorder(10, 5, 5, 5)
        }
      }
    } else new BoxPanel(Orientation.Horizontal) // Niezalogowany nic nie zrobi...
  }

  /**
   * Wyświetlanie czegoś
   */
  // Lista funkcji... seems legit
  private var additionalCols : List[(String) => Button] = List()
  // Lista elementów w określonej kategorii
  def elementsList(pageName : String, uri : String,
    columns : List[Map[String, String]],
    editUri : String = "", delUri : String = "",
    editExcludeIds : Set[String] = Set(), delExcludeIds : Set[String] = Set()
  ) : Unit = {
    val siteData = WebAPI.read(uri)
    var rowId = 1
    val wnd : GridBagPanel = new GridBagPanel {
      def constraints(x : Int, y : Int, gridWidth: Int = 1, gridHeight: Int = 1, weightx: Double = 0.0, weighty : Double = 0.0, fill: GridBagPanel.Fill.Value = GridBagPanel.Fill.None) : Constraints = {
        val c = new Constraints
        c.gridx = x
        c.gridy = y
        c.gridwidth = gridWidth
        c.gridheight = gridHeight
        c.weightx = weightx
        c.weighty = weighty
        c.fill = fill
        c.insets = new Insets(5, 8, 5, 8)
        c
      }
      // Nagłówki dla danych
      var colId = 0
      columns.foreach{column => {
        add(new Label("<html>"+column.head._1) {
            horizontalAlignment=Alignment.Left
        }, constraints(colId, 0, fill=GridBagPanel.Fill.Both))
        colId+=1
      }}
      // Wypełnienie danymi
      siteData.foreach{row => {
        colId = 0
        columns.foreach{column => {
            add(new Label("<html>"+row(column.head._2)) { horizontalAlignment=Alignment.Left }, constraints(colId, rowId, fill=GridBagPanel.Fill.Both))
            colId+=1
        }}
        // Button edycji elementu
        if( ! editUri.equals("") && ! editExcludeIds.exists(_ == row("id"))) { // Pomijamy edycję określonych pól
          val editBtn = new Button("Edytuj") {
            listenTo(this)
            reactions += { case ButtonClicked(_) => addEditAction(editUri replaceAll("\\<id\\>", row("id")), pageName, mainMenu, "Edycja elementu") }
          }
          colId+=1
          add(editBtn, constraints(colId, rowId, fill=GridBagPanel.Fill.Both))
        }
        if( ! delUri.equals("") && ! delExcludeIds.exists(_ == row("id"))) { // Pomijamy usuwanie określonych pól
          val delBtn = new Button("Usuń") {
            listenTo(this)
            reactions += { case ButtonClicked(_) => delAction(delUri replaceAll("\\<id\\>", row("id")), pageName, uri, columns, editUri, delUri, editExcludeIds, delExcludeIds) }
          }
          colId+=1
          add(delBtn, constraints(colId, rowId, fill=GridBagPanel.Fill.Both))
        }
        // Dopisywanie dodatkowych kolumn
        additionalCols.foreach{fun => add(fun(row("id")), constraints(colId, rowId, fill=GridBagPanel.Fill.Both)) }
        rowId+=1
      }}
    }
    // Repaint okienka
    repaintFunc(new BoxPanel(Orientation.Vertical) {
      if(rowId != 0) {
        contents += new ScrollPane(wnd) {
          preferredSize = new Dimension( // Okienko max 900x500px
            if(wnd.preferredSize.width <= 900) wnd.preferredSize.width + 20 else 900,
            if(wnd.preferredSize.height <= 500) wnd.preferredSize.height + 20 else 500
          )
          maximumSize = preferredSize
        }
      } else contents += new Label("Brak wpisów")
    }, pageName)
  }

  /**
   * Akcja dodawania/edycji
   */
  def addEditAction(uri : String, oldPageName : String, repaintTo : BoxPanel, newName : String) : Unit = repaintFunc(getForms(uri, repaintTo, oldPageName), newName)

  /**
   * Akcja usuwania elementu
   */
  def delAction(uri : String, pageName : String,
    backUri : String,
    columns : List[Map[String, String]],
    editUri : String = "", delUri : String = "",
    editExcludeIds : Set[String] = Set(), delExcludeIds : Set[String] = Set()
  ) : Unit = { // Akcja dla usuwania elementu
    val choice = Dialog.showConfirmation(frameContent, "Czy na pewno chcesz usunąć ten element?", "Potwierdzenie", Dialog.Options.OkCancel, Dialog.Message.Warning)
    if(Dialog.Result.Ok == choice) {
      if(WebAPI.delete(uri)) elementsList(pageName, backUri, columns, editUri, delUri, editExcludeIds, delExcludeIds)
      else Dialog.showMessage(frameContent, "Wystąpił błąd podczas usuwania", "Błąd")
    }
  }
  
//  def cancelOwnVisit()
}
