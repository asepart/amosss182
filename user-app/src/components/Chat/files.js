import {URL} from '../Login/const';
import {getAuth, getAuthForMediaPost} from '../Login/auth';
import {setMsg, sendMessage} from './sendMessages';
import moment from 'moment';

var link = '';

export function uploadFile (uri, ticket) {
	
	//TODO: get extension from uri/file
	var ext = '';

	var filename = (btoa(moment()) + ext);
	
	//send  filename to chat
	var tmp = new Date();
	var date = tmp.toDateString();
	var time = tmp.toTimeString().slice(0,8);
	var timestamp = "[" + date + ", " + time + "]";
	setMsg(timestamp + ": " + filename);
	sendMessage();
	
	//convert file into FormData
	const image = {
		      uri: uri,
		      type: 'multipart/form-data',
		      name: filename
	}
	const imgBody = new FormData();
	imgBody.append('file', image);
	
	//send file to backend
	fetch(URL + '/files/' + ticket, {
        method: 'POST',
        headers: getAuthForMediaPost(),
        body: imgBody
	}).then(
		    response => {
		    	response.json();
		    	//console.log(JSON.stringify(response));
		    	if (response.status == '409') {
		    		alert("File upload failed. Filename already exists.");
		    	}
		    }
	  ).catch(
	    error => {
	    	console.log('uploadImage error:', error);
	    	alert("File upload failed.");
	    }
	  );
	
	console.log(ticket);
	console.log(imgBody);
}

export async function getDownloadLink (filename, ticket) {
	
	//get downloadlink from backend
	const res = await fetch(URL + '/files/' + ticket + '/' + filename + '?thumbnail=false', {
        method: 'GET',
        headers: getAuth()
	}).then(
		    response => {
		    	response.json();
		    	link = '';
		    	if (response.status == '200') {
		    		link = response.url;
		    	} else if (response.status == '404') {
		    		alert("File does not exist.");
		    	}
		    }
	  ).catch(
	    error => {
	    	console.log('downloadImage error:', error);
	    	alert("File download failed.");
	    }
	  );
	return link;	
}