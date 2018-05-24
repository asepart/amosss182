import {URL} from '../shared/const';
import {getAuth} from '../shared/auth';

export var ticket = "";
export var msg = "";

//window.btoa = require('Base64').btoa;

export function setMsg(mes) {
	msg = mes;
}

export function setTicketID(tid) {
    ticket = tid;
}

export async function sendMessage() {
     fetch(URL + '/messages/' + ticket, {
            method: 'POST',
            headers: getAuth(),
            body:  msg
        })

    }
