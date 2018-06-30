import React, { Component } from 'react';
import CPopup from '../shared/CPopup';
import { URL } from '../shared/const';
import { View, Text } from 'react-native';

export default class ChatImage extends Component {

	render() {
		if (this.props.src == null){
			return (<View></View>)
		}
		return (
			<CPopup toggle={this.props.src + '?thumbnail=true'}>
				<img src={this.props.src} alt="image"/>
			</CPopup>
		);

	}
}
