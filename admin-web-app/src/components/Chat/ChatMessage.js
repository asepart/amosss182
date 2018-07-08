import React, { Component } from 'react';
import { View, Text } from 'react-native';
import ChatImage from './ChatImage';
import ChatVideo from './ChatVideo';
import {URL} from '../shared/const';

function isImage (str) {
	if (typeof str === "string"){
		if(str.length < 5) // .jpg, .png, .bmp is always >= 5 chars
			return false;
		if(str.length - str.indexOf(".jpg") === 4  ||
			str.length - str.indexOf(".png") === 4 ||
			str.length - str.indexOf(".bmp") === 4)
			return true;
	}
	return false;
}

function isVideo (str) {
	if (typeof str === "string"){
		if(str.length < 5) // .jpg, .png, .bmp is always >= 5 chars
			return false;
		if(str.length - str.indexOf(".mp4") === 4  ||
			str.length - str.indexOf(".mov") === 4 ||
			str.length - str.indexOf(".mkv") === 4)
			return true;
	}
	return false;
}

export default class ChatMessage extends Component {
	render() {
		if (this.props.msg.attachment == null) {
			return (<View><Text>{this.props.msg.content}</Text></View>)
		} else {
			return(
				<View>
					{
						isImage(this.props.msg.attachment)?
							<ChatImage src={URL + '/files/' + this.props.ticket + '/' + this.props.msg.attachment}/>
						: <View></View>
					}
					{
						isVideo(this.props.msg.attachment)?
							<ChatVideo src={URL + '/files/' + this.props.ticket + '/' + this.props.msg.attachment}/>
						: <View></View>
					}
					<a href={this.props.msg.content}>{this.props.msg.content}</a>
				</View>
			);
		}
	}
}
