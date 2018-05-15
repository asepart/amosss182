import Cookies from 'universal-cookie';

export const cki = new Cookies(document.cookie);
export const URL = getURL();

function getURL(){
	if (window.location.hostname.indexOf("dev.herokuapp") !== -1){
		return "https://asepartback-dev.herokuapp.com";
	} else if (window.location.hostname.indexOf("localhost") !== -1){
		return "http://localhost:12345";
	}
	return "https://asepartweb.herokuapp.com";
}
