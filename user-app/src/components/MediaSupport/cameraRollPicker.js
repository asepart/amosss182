import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Button
} from 'react-native';
import {setTicketID, msg, ticket} from '../Chat/sendMessages';
import {uploadFile} from '../Chat/files';
import CameraRollPicker from 'react-native-camera-roll-picker';

var buttonEnabled = false;

export default class CameraRollPicer extends Component {
	
	static navigationOptions = ({ navigation }) => {
		const { params = {} } = navigation.state
		return {
		title: 'Photo & Video Library',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		},
		headerRight: <Button title={"Send"} onPress={ () => params.send() } disabled={!buttonEnabled} />
		}
	}
	
	constructor(props) {
		super(props);
		
		this.state = {
				num: 0,
				selected: [],
		};
		
		buttonEnabled = false;
	}
	
	componentDidMount () {
	    this.props.navigation.setParams({ send: this.sendFile })
	  }
	
	sendFile = () => {
		
		//send file to backend
		for(let i = 0; i < this.state.num; i++){
			
			setTimeout(() => {
				 
				uploadFile(this.state.selected[i].uri, ticket);
			 
			    }, i*1000);
		}
		
		//navigate back to chat
		const { navigate } = this.props.navigation;
		navigate("Seventh", { name: "GetMessages" });
	}
	
	getSelectedImages(images, current) {
		var num = images.length;
		
		this.setState({
			num: num,
			selected: images,
		});
		
		buttonEnabled = (num >= 1);
		
		const { navigate } = this.props.navigation;
		navigate("Eleventh", { refresh: "CameraRollPicker" });
		
		console.log(current);
		console.log(this.state.selected);
	}
	
	render() {
		return (
			<View style={styles.container}>
				<View style={styles.content}>
					<Text style={styles.text}>
						<Text style={styles.bold}>{this.state.num}</Text> file(s) selected
					</Text>
				</View>
				<CameraRollPicker
					scrollRenderAheadDistance={500}
					initialListSize={1}
					pageSize={3}
					removeClippedSubviews={false}
					groupTypes='SavedPhotos'
					batchSize={5}
					maximum={3}
					selected={this.state.selected}
					selectSingleItem={false}
					assetType='All'
					imagesPerRow={3}
					imageMargin={5}
					callback={this.getSelectedImages.bind(this)} />
			</View>
		);
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: '#5daedb',
	},
	content: {
		height: 30,
		flexDirection: 'row',
		justifyContent: 'center',
		alignItems: 'center',
		flexWrap: 'wrap',
	},
	text: {
		fontSize: 16,
		alignItems: 'center',
		color: '#fff',
	},
	bold: {
		fontWeight: 'bold',
	},
	info: {
		fontSize: 12,
	},
});