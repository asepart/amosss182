import {URL} from '../Login/const';
import {getAuth} from '../Login/auth';
import {ProjectInfo} from '../Projects/projectInfo';

export var msg = "";


window.btoa = require('Base64').btoa;

export function setMsg(mes) {
	msg = mes;
}


export async function sendMessage() {
     fetch(URL + '/messages/1' ,{
            method: 'POST',
            headers: getAuth(),
            body:  msg
        })
      
    }
    
 