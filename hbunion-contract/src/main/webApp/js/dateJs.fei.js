var date=new Date();var nowYear=date.getFullYear();var nowMonth=date.getMonth()+1;var nowDay=date.getDate();var splitString="-";var weekDays=new Array("日","一","二","三","四","五","六");var months=new Array("一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月");var lastDays=new Array(31,28,31,30,31,30,31,31,30,31,30,31);var checkYear=nowYear;var checkMonth=nowMonth;function setInput(selectDay){document.getElementById('txt_calendar').value=checkYear+splitString+checkMonth+splitString+selectDay;hidDate();}
function showDate(){createDate(nowYear,nowMonth);var x=document.getElementById('txt_calendar').offsetLeft;var y=document.getElementById('txt_calendar').offsetTop+22;document.getElementById('dateOuter').style.left=x+"px";document.getElementById('dateOuter').style.top=y+"px";document.getElementById('dateOuter').style.display="";}
var outerStyle="display: none;position: absolute;border-left:1px solid #cccccc;border-right:1px solid #ccc;border-bottom:1px solid #ccc;border-bottom-left-radius:3px;border-bottom-right-radius:3px;z-index:5;width: 314px;margin-top:15px;background-color: #F7F7F7;color: #1d61ec;padding-bottom: 20px;";document.write('<div style="'+outerStyle+'" id="dateOuter"></div>')
function createDate(thisYear,thisMonth){var createDoc='<div style="height: 30px;">';createDoc+='<p style="width: 100%;height: 30px;line-height:30px;text-align: center;color: #999;" onclick="getThisDay()">当前日期 '+nowYear+"年"+nowMonth+"月"+nowDay+"号";createDoc+='<span id="closeDate" onClick="hidDate()" style="float: right;font-size: 25px;margin: -5px 3px 0 0;cursor: pointer;">×</span></p></div>';;createDoc+='<div style="margin-bottom: 8px;margin-top:10px">';createDoc+='<span id="lastMonth" onclick="lastMonthClick()" style="margin: 0 20px 0 25px;cursor:pointer;"><</span>';createDoc+='<select id ="selectYear" class="selectStyle" onchange="changeYearAndMonth()">';for(var i=1900;i<=2099;i++){createDoc+="<option value="+i+">"+i+"</option>";}
createDoc+="</select>年";createDoc+='<select id ="selectMonth" class="selectStyle" onchange="changeYearAndMonth()">';for(var i=1;i<=12;i++){createDoc+="<option value="+i+">"+i+"</option>";}
createDoc+="</select>月";createDoc+='<span id="nextMonth" onClick="nextMonthClick()" style="float: right;margin-right: 25px;cursor:pointer;">></span></div>';createDoc+='<div class="everyWeekDay">';for(var i=0;i<weekDays.length;i++){if(weekDays[i]=="日"||weekDays[i]=="六"){createDoc+='<span class="weekday" style="color:red;">'+weekDays[i]+'</span>'}else{createDoc+='<span class="weekday">'+weekDays[i]+'</span>'}}
createDoc+='</div>';createDoc+='<div class="everyDay"><div class="marginTop">';var thisWeek=getThisWeekDay(thisYear,thisMonth,1);if(thisWeek!=0){for(var i=0;i<thisWeek;i++){createDoc+='<span class="days"></span>';}}
for(var i=1;i<getThisMonthDay(thisYear,thisMonth)+1;i++){if(thisYear==nowYear&&thisMonth==nowMonth&&i==nowDay){if(getThisWeekDay(thisYear,thisMonth,i)==6||getThisWeekDay(thisYear,thisMonth,i)==0){createDoc+='<span onclick="setInput('+i+')" class="days" style="background-color:#4eccc4;color:#FFFFFF;cursor:pointer;">'+i+'</span>';}else{createDoc+='<span onClick="setInput('+i+')" class="days" style="background-color:#4eccc4;color:#FFFFFF;cursor:pointer;">'+i+'</span>';}}else{if(getThisWeekDay(thisYear,thisMonth,i)==6||getThisWeekDay(thisYear,thisMonth,i)==0){createDoc+='<span id="weekends" onClick="setInput('+i+')" class="days" onmouseover="mouseOver(this);" onmouseout="mouseOut(this)" style="color:red;cursor:pointer;">'+i+'</span>';}else{createDoc+='<span onClick="setInput('+i+')" class="days" onmouseover="mouseOver(this);" onmouseout="mouseOut(this)" style="cursor:pointer;">'+i+'</span>';}}
if(getThisWeekDay(thisYear,thisMonth,i)==6){createDoc+="</tr>";}}
createDoc+='</div></div>';document.getElementById('dateOuter').innerHTML=createDoc;document.getElementById('selectYear').value=thisYear;document.getElementById('selectMonth').value=thisMonth;}
function getThisDay(){checkYear=nowYear;checkMonth=nowMonth;createDate(checkYear,checkMonth);}
function lastMonthClick(){if(checkMonth==1){checkYear=checkYear-1;checkMonth=12;}else{checkMonth=checkMonth-1;}
createDate(checkYear,checkMonth);}
function nextMonthClick(){if(checkMonth==12){checkYear=checkYear+1;checkMonth=1;}else{checkMonth=checkMonth+1;}
createDate(checkYear,checkMonth);}
function changeYearAndMonth(){checkYear=document.getElementById('selectYear').value;checkMonth=document.getElementById('selectMonth').value;createDate(checkYear,checkMonth);}
function isLeapYear(year){var isLeap=false;if(0==year%4&&((year%100!=0)||(year%400==0))){isLeap=true;}
return isLeap;}
function getThisMonthDay(year,month){var thisDayCount=lastDays[month-1];if((month==2)&&isLeapYear(year)){thisDayCount++;}
return thisDayCount;}
function getThisWeekDay(year,month,date){var thisDate=new Date(year,month-1,date);return thisDate.getDay();}
function mouseOver(obj){if(obj.id=="weekends"){obj.style.border="1px solid red";}else{obj.style.border="1px solid #4eccc4";}}
function mouseOut(obj){obj.style.border="1px solid #F7F7F7";}
function hidDate(){document.getElementById('dateOuter').style.display="none";}
document.onkeydown=function(e){var thisEvent=e||window.event;var keyCode=thisEvent.keyCode||thisEvent.which;if(document.getElementById('dateOuter').style.display=="none"){return false;}
switch(keyCode){case 32:getThisDay();break;case 27:hiddenCal();break;case 37:lastMonthClick();break;case 39:nextMonthClick();break;}}
var direct;var scrollFunc=function(e){var thisEvent=e||window.event;if(thisEvent.wheelDelta){direct=thisEvent.wheelDelta;}
else if(thisEvent.detail){direct=thisEvent.detail;}
if(document.getElementById('dateOuter').style.display=="none"){return false;}
if(direct=="120"||direct=="3"){lastMonthClick();}else if(direct=="-120"||direct=="-3"){nextMonthClick();}}
if(document.addEventListener){if(myBrowser()=="FF"){document.addEventListener('DOMMouseScroll',scrollFunc,false);}else{document.addEventListener('mousewheel',scrollFunc,false);}}else{document.onmousewheel=scrollFunc;}
function myBrowser(){var userAgent=navigator.userAgent;var isOpera=userAgent.indexOf("Opera")>-1;if(isOpera){return "Opera"};if(userAgent.indexOf("Firefox")>-1){return "FF";}
if(userAgent.indexOf("Chrome")>-1){return "Chrome";}
if(userAgent.indexOf("Safari")>-1){return "Safari";}
if(userAgent.indexOf("compatible")>-1&&userAgent.indexOf("MSIE")>-1&&!isOpera){return "IE";};if(userAgent.indexOf("Trident")>-1){return "Edge";}}