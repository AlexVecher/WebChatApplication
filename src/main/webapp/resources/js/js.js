var uniqueId = function() {
  var date = Date.now();
  var random = Math.random() * Math.random();

  return Math.floor(date * random).toString();
};

var theMessage = function(text1, text2) {
  return {
  	name:text1,
    message:text2,
    id: uniqueId()
  };
};

var theChangeMessage = function(text1, text2, value) {
    return {
      name:text1,
      message:text2,
      id: value.toString()
    };
};

var account = function(textName, done) {
  return {
    name:textName,
    done: !!done,
    id: uniqueId()
  };
};

var appState = {
  mainUrl : 'chat',
  messList:[],
  token : 'TE11EN'
};

var accountInfo = [];

function run(){
  var appContainer = document.getElementsByClassName('chat')[0];
  appContainer.addEventListener('click', delegateEvent);
  updateMessages();
  var accInfo = restore("acc");
  if (accInfo != null) {
  createAccountInfo(accInfo);
}
}

function createAllMessages(allMessages) {
  for(var i = 0; i < allMessages.length; i++)
    addOldMessage(allMessages[i]);
}

function delegateEvent(evtObj) {
  if(evtObj.type === 'click' && evtObj.target.classList.contains('signin1')){
    show('block');
  }
  if(evtObj.type === 'click' && evtObj.target.classList.contains('signin2')){
    nameButtonClick(evtObj);
  }
  if(evtObj.type === 'click' && evtObj.target.classList.contains('send')){
    sendButtonClick(evtObj);
  }
  if(evtObj.type === 'click' && evtObj.target.classList.contains('logout')){
    logOutButtonClick(evtObj);
  }
}

function editName(obj) {
  document.getElementById('whitewindow2').style.display = 'block';
  document.getElementById('block').style.display = 'block';
  document.getElementById('nameEdit').value = document.getElementById('td1_1').innerHTML;
}

function saveName() {
  var oldName = document.getElementById('td1_1').innerHTML;
  var editName = document.getElementById('nameEdit').value;
  if (!editName) return;
  if (oldName == editName) return;
  document.getElementById('td1_1').innerHTML = editName;
  updateLocalStorage(editName, oldName);
}

function updateLocalStorage(editName, oldName) {
  accountInfo[0].name = editName;
  store(accountInfo, "acc");
  location.reload();
}

function sendButtonClick(){
  var tr11 = document.getElementById('td1_1');
  var message = document.getElementById('message');
  message.value = message.value.replace(/\n/g, "<br />");
  var newMessage = theMessage(tr11.innerHTML, message.value);
  if(message.value == '')
    return;
  storeMessages(newMessage);
  message.value = '';
}

function storeMessages(sendMessage, continueWith) {
     post(appState.mainUrl, JSON.stringify(sendMessage), function () {
    });
}

function addOldMessage(task) {
  var messList = appState.messList;
  messList.push(task);
  var tr11 = document.getElementById('td1_1');
  var HTMLTextButtonDelete = '<input type="button" value = "Delete" onClick="deleteRow(this)" class="btn btn-primary">';
  var HTMLTextButtonEdit = '<input type="button" value = "Edit" onClick="editRow(this)" class="btn btn-primary">';
  var table = document.getElementById('table2');
  var rowCount = table.rows.length;
  var row = table.insertRow(rowCount);
  if(task.name == tr11.innerHTML) {
    row.insertCell(0).innerHTML = HTMLTextButtonEdit;
    row.insertCell(1).innerHTML = HTMLTextButtonDelete;
  }
  else {
    row.insertCell(0).innerHTML = "";
    row.insertCell(1).innerHTML = "";
  }
  row.insertCell(2).innerHTML = task.name;
  row.insertCell(3).innerHTML = task.message; 
  var elem = document.getElementById('sc');
  elem.scrollTop = elem.scrollHeight;
}

function sleep(ms) {
ms += new Date().getTime();
while (new Date() < ms){}
} 

function saveRow() {
  var message = document.getElementById('editMessage');
  message.value = message.value.replace(/\n/g, "<br />");
  if(message.value == '')
    return;
  document.getElementById('whitewindow3').style.display = 'none';
  document.getElementById('block').style.display = 'none';
  var tr11 = document.getElementById('td1_1').innerHTML;
  var messList = appState.messList;
  var item = theChangeMessage(tr11, message.value, messList[indexEditRow-1].id);
  changeMessages(item.id, item);
  sleep(100);
}

function deleteRow(obj) {
  var messList = appState.messList;
  var index = obj.parentNode.parentNode.rowIndex;
  var m = theChangeMessage(messList[index-1].name, messList[index-1].message, messList[index-1].id);
  deleteMessage(messList[index-1].id, m,
      function(){
      });
  messList[index - 1].message = "Deleted";
  messList[index - 1].isDeleted = "true";
  var table = document.getElementById('table2');
  obj.parentNode.parentNode.cells[3].innerHTML = "Deleted";
}

function deleteMessage(index, msg, continueWith) {
  var indexToken = index * 8 + 11;
  var url = appState.mainUrl + '?token=' + "TN" +indexToken.toString() + "EN";
  del(url, JSON.stringify(msg), function () {
    updateMessages();
  });
}

function changeMessages(index, changeMessage, continueWith) {
  var indexToken = index * 8 + 11;
  var url = appState.mainUrl + '?token=' + "TN" + indexToken + "EN";
  put(url, JSON.stringify(changeMessage), function () {
    updateMessages();
  });
}

function rrestore(continueWith) {
  var url = appState.mainUrl + '?token=' + appState.token;

  get(url, function(responseText) {
    console.assert(responseText != null);

    var response = JSON.parse(responseText);

    createAllMessages(response.messages);

    continueWith && continueWith();
  });
}

function restore(str) {
  if(typeof(Storage) == "undefined") {
    alert('localStorage is not accessible');
    return 0;
  }

  var item = localStorage.getItem(str);

  return item && JSON.parse(item);
}

function createAccountInfo(accI) {
  if (accI[0].done == true) {
    document.getElementById('logout').style.display = 'block'; 
    document.getElementById('send').style.display = 'block'; 
    document.getElementById('signIn1').style.display = 'none';

    HTMLTextButtonEditName = '<input type="button" value = "Edit" onClick="editName(this)" class="btn btn-primary">';
    var tr22 = document.getElementById('td2_2');
    tr22.innerHTML = HTMLTextButtonEditName;
    var tr11 = document.getElementById('td1_1');
    var table = document.getElementById('table3');
    tr11.innerHTML = accI[0].name;
    table.appendChild(tr11);
    table.appendChild(tr22);
    var newAcc = account(accI.name, true);
    accountInfo.push(newAcc);
  }
}

function nameButtonClick(){
  document.getElementById('signIn1').style.display = 'none';
  document.getElementById('logout').style.display = 'block'; 
  var name = document.getElementById('name');
  if(!name.value){
    return;
  }
  var newAcc = account(name.value, true);
  accountInfo.push(newAcc);
  addName(name.value);
  name.value = '';
  store(accountInfo, "acc");
  location.reload();
} 

function addName(acc) {
  document.getElementById('send').style.display = 'block'; 
  show('none');
  var tr11 = document.getElementById('td1_1');
  var table = document.getElementById('table3');
  tr11.innerHTML = acc;
}

function logOutButtonClick(){
  localStorage.removeItem("acc");
  document.getElementById('logout').style.display = 'none'; 
  document.getElementById('send').style.display = 'none'; 
  document.getElementById('signIn1').style.display = 'block';
  var tr11 = document.getElementById('td1_1');
  tr11.innerHTML = "";
  show('none');
  location.reload();
} 

function store(listToSave, str) {
  if(typeof(Storage) == "undefined") {
    alert('localStorage is not accessible');
    return;
  }
  localStorage.setItem(str, JSON.stringify(listToSave));
}

var indexEditRow = 0;
function editRow(obj) {
  var messList = appState.messList;
  indexEditRow = obj.parentNode.parentNode.rowIndex;
  if (messList[indexEditRow - 1] == "true") return;
  document.getElementById('whitewindow3').style.display = 'block';
  document.getElementById('block').style.display = 'block';
  document.getElementById('editMessage').innerHTML = obj.parentNode.parentNode.cells[3].innerHTML.replace(/<br>/gi, '\n');
  var editText = obj.parentNode.parentNode.cells[3].innerHTML;
}

function updateMessages(continueWith) {
  var url = appState.mainUrl + '?token=' + appState.token;
  var table = document.getElementById('table2');
  var ind = table.rows.length - 1;
  get(url, function (responseText) {
    console.assert(responseText != null);
    var response = JSON.parse(responseText).messages;
    for (var i = 0; i < response.length; i++) {
      var message = response[i];
      if(parseInt(message.countMess) >= ind)
      {
        addOldMessage(message);
      }
    }
    continueWith && continueWith();
  });
  setTimeout(updateMessages, 1000);

}

function post(url, data, continueWith, continueWithError) {
  ajax('POST', url, data, continueWith, continueWithError); 
}

function get(url, continueWith, continueWithError) {
  ajax('GET', url, null, continueWith, continueWithError);
}

function del(url, data, continueWith, continueWithError) {
  ajax('DELETE', url, data, continueWith, continueWithError);
};

function put(url, data, continueWith, continueWithError) {
    ajax('PUT', url, data, continueWith, continueWithError);
}

function defaultErrorHandler(message) {

}

function isError(text) {
  if(text == "")
    return false;
  
  try {
    var obj = JSON.parse(text);
  } catch(ex) {
    return true;
  }

  return !!obj.error;
}

function ajax(method, url, data, continueWith, continueWithError) {
  var xhr = new XMLHttpRequest();

  continueWithError = continueWithError || defaultErrorHandler;
  xhr.open(method || 'GET', url, true);

  xhr.onload = function () {
    if (xhr.readyState !== 4)
      return;

    if(xhr.status != 200) {
      continueWithError('Error on the server side, response ' + xhr.status);
      return;
    }

    if(isError(xhr.responseText)) {
      continueWithError('Error on the server side, response ' + xhr.responseText);
      return;
    }

    continueWith(xhr.responseText);
  };    

    xhr.ontimeout = function () {
      ontinueWithError('Server timed out !');
    }

    xhr.onerror = function (e) {
      var errMsg = 'Server connection error !\n'+
      '\n' +
      'Check if \n'+
      '- server is active\n'+
      '- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
}

function show(state){
  document.getElementById('block').style.display = state;  
  document.getElementById('whitewindow1').style.display = state;    
}
