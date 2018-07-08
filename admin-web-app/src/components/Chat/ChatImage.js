import React, { Component } from 'react';
import CPopup from '../shared/CPopup';
import { View } from 'react-native';

export class ChatImage extends Component {

	render() {
		if (this.props.src == null){
			return (<View></View>)
		}
		return (
			<CPopup toggle={this.props.src + '?thumbnail=true'}>
				<img src={this.props.src} alt="Preview"/>
			</CPopup>
		);

	}
}
