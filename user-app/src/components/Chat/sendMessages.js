import {URL} from '../Login/const';
import {getAuth} from '../Login/auth';

export var msg = "";


window.btoa = require('Base64').btoa;

export function setMsg(mes) {
	msg = mes;
}


export async function sendMessage() {
     fetch(URL + '/messages/3', {
            method: 'POST',
            headers: getAuth(),
            body:  msg
        })
      
    }
    
 