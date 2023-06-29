
// Calculator
var calc_button  = document.getElementById('calc_button');

function launch_calc()
{
   var oShell = new ActiveXObject("WScript.Shell");
   oShell.Run('"' +'C:\\Windows\\System32\\calc.exe'  + '"', 1);
}
calc_button.addEventListener("click", launch_calc);



// 1c
var one_c_button = document.getElementById('1c_button');

function launch_1c(){
  var oShell = new ActiveXObject("WScript.Shell");
  oShell.Run('"' +'C:\\Bases\\БСП\\БСП.v8i'  + '"', 1);
}

one_c_button.addEventListener("click", launch_1c);






// help button
var help_button = document.getElementById('help_button');

function show_about_us()
{
   var oShell = new ActiveXObject("WScript.Shell");
   oShell.Run('"' +'C:\\Users\\Serv12_1\\Desktop\\grid\\about_us\\about_us.hta'  + '"', 1);
}
help_button.addEventListener("click", show_about_us);



// off button
var off_button = document.getElementById('off_button');



// notification

var notification_button = document.getElementById('notification_button');
