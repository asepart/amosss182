import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Button
} from 'react-native';
import {setMsg, sendMessage, setTicketID, msg, ticket} from '../Chat/sendMessages';
import {uploadFile} from '../Chat/uploadFile';
import CameraRollPicker from 'react-native-camera-roll-picker';
//import fileType from 'react-native-file-type';

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
		
		//create new filename
/*		var ext = '';
		fileType(this.state.selected[0].uri).then((type) => {
		    //Ext: type.ext
		    //MimeType: type.mime
			ext = type.ext;
		})
		const filename = (Date() + '.' + ext); console.log(filename);
*/		const filename = ('TESTJPG-' + Date() + '.jpg')
		
		//send  flename to chat
		var tmp = new Date();
		var date = tmp.toDateString();
		var time = tmp.toTimeString().slice(0,8);
		var timestamp = "[" + date + ", " + time + "]";
		setMsg(timestamp + ": " + filename);
		sendMessage();
		
		//convert file into FormData
		const image = {
			      uri: this.state.selected[0].uri,
			      type: 'multipart/form-data',
			      name: filename
		}
		const imgBody = new FormData();
		imgBody.append('file', image);
		
		//send file to backend
		uploadFile(imgBody, ticket);
		
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
		navigate("Tenth", { refresh: "CameraRollPicker" });
		
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
					selectSingleItem={true}
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