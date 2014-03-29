package pl.vot.tomekby.Controller

import swing._
import pl.vot.tomekby.Model._

class AboutScala extends Dialog {
  
  contents = new ScrollPane(new Label("<html>"+WebAPI.getMain)) {
    preferredSize = new Dimension(900, 500)
    maximumSize = preferredSize
  }
  title = "O programie i stronie"
  resizable = false
  modal = true
  centerOnScreen
  open
}
