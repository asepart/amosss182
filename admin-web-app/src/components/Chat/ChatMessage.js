import React, { Component } from 'react';
import { View, Text } from 'react-native';
import ChatImage from './ChatImage';
import ChatVideo from './ChatVideo';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';

function isImage (str) {
	return true;
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

export default class ChatMessage extends Component {
	render() {
		if (this.props.children.attachment == null) {
			return (<View><Text>{this.props.children.content}</Text></View>)
		} else {
			return(
				<View>
					{
						isImage(this.props.children.attachment)?
							<ChatImage src={this.props.children.attachment}/>
						: <View></View>
					}
					<Text>{this.props.children.content}</Text>
				</View>
			);
		}
	}
}
