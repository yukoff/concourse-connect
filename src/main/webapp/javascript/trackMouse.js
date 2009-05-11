// Global variables
xMousePos = 0; // Horizontal position of the mouse on the screen
yMousePos = 0; // Vertical position of the mouse on the screen
xMousePosMax = 0; // Width of the page
yMousePosMax = 0; // Height of the page

function captureMousePosition(e) {
  if (document.all) {
    xMousePos = window.event.x+document.body.scrollLeft;
    yMousePos = window.event.y+document.body.scrollTop;
    xMousePosMax = document.body.clientWidth+document.body.scrollLeft;
    yMousePosMax = document.body.clientHeight+document.body.scrollTop;
  } else if (document.getElementById || document.layers) {
    xMousePos = e.pageX;
    yMousePos = e.pageY;
    xMousePosMax = window.innerWidth+window.pageXOffset;
    yMousePosMax = window.innerHeight+window.pageYOffset;
  }
  //window.status = "xMousePos=" + xMousePos + ", yMousePos=" + yMousePos +
  //", xMousePosMax=" + xMousePosMax + ", yMousePosMax=" + yMousePosMax;
}

function trackMouse() {
  if (document.all) {
      document.onmousemove = captureMousePosition;
  } else if (document.getElementById) {
      document.onmousemove = captureMousePosition;
  }
}
YAHOO.util.Event.onDOMReady(trackMouse);
