

var height = screen.height;
var width  = screen.width;


window.resizeTo (width, height);
window.moveTo(0, 0);





document.oncontextmenu = function(e){
 var evt = new Object({keyCode:93});

}
function stopEvent(event){
 if(event.preventDefault != undefined)
  event.preventDefault();
 if(event.stopPropagation != undefined)
  event.stopPropagation();
}


document.oncontextmenu = function (){return false};
