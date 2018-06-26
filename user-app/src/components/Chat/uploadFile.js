import {URL} from '../Login/const';
import {getAuthForMediaPost} from '../Login/auth';
import {setMsg, sendMessage} from './sendMessages';
//import fileType from 'react-native-file-type';

export function uploadFile (uri, ticket) {
	
	//TODO: import fileType correctly, then enable commented code
	
	//create new filename
	var ext = '';
/*		fileType(this.state.selected[0].uri).then((type) => {
	    //Ext: type.ext
	    //MimeType: type.mime
		ext = type.ext;
	})
	var filename = (Date() + '.' + ext);
*/	var filename = Date();
	
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