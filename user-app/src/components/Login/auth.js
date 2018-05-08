import {URL} from './const';

var username = "";
var psw = "";
var lastAuth = 0;
export var watcher = {counter: 0};

export function setPSW(lpsw) {
	psw = lpsw;
}
export function setUsername(lUsername) {
	username = lUsername;
}
export function revokeAuth() {
	lastAuth = 0;
}

export function getAuth() {
	let headers = new Headers();
	headers.append('Accept', 'application/json');
	headers.append('X-ASEPART-Role', 'User');
	headers.append('Authorization', 'Basic ' + btoa(username + ":" + psw));
	return (headers);
}

export async function authenticate() {
	var response = await fetch(URL + '/login', {
		method: 'GET',
		headers: getAuth
	})
		switch (response.status) {
			case 200:
				lastAuth = Date.now();
				return true;
			case 401:
				lastAuth = false;
				return false;
			default:
				console.error("Error:" + response.status);
				console.error(response.text);
		}

}

export function isAuth() {
	if (new Date(Date.now() - lastAuth).getMinutes() < 5 && lastAuth === true) {
		return true;
	}
	return authenticate();
}