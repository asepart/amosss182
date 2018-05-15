import {URL} from '../Login/const';

export var username = "";
export var psw = "";
var auth=false;

window.btoa = require('Base64').btoa;

export function setPSW(lpsw) {
	psw = lpsw;
}
export function setUsername(lUsername) {
	username = lUsername;
}

export function getAuth() {
	
	return {
		
		'Accept': 'application/json',
		'X-ASEPART-Role': 'User',
		'Authorization': 'Basic ' + btoa(username + ":" + psw)
	};
}

async function authenticate() {
	var response = await fetch(URL + '/login', {
		method: 'GET',
		headers: getAuth()
	})
	switch (response.status) {
		case 200:
			auth=true;
			return true;
		case 401:
			auth=false;
			return false;
		default:
			console.error("Error:" + response.status);
			console.error(response.text);
	}
}

export async function isAuth() {
	if (auth)
		return true;
	if (username === '' || psw === '')
		return false;
	return await authenticate();
}