import {URL} from '../shared/const';
import {getAuth} from '../shared/auth';

export var ticket = "";
export var msg = "";
export var attachment = "";

//window.btoa = require('Base64').btoa;

export function setMsg(mes) {
	msg = mes;
}

export function setTicketID(tid) {
		ticket = tid;
}

export function setAttachment(att) {
		attachment = att;
}

export async function sendMessage() {
		 fetch(URL + '/messages/' + ticket, {
						method: 'POST',
						headers: getAuth(),
						body:	msg
				})
		}

export async function sendAttachment() {
	fetch(URL + '/messages/' + ticket + '?attachment=' + attachment, {
		method: 'POST',
		headers: getAuth(),
		body:	msg
	})
}
