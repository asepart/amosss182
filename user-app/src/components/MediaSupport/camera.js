import React, {Component} from 'react';
import {
  AppRegistry,
  Dimensions,
  StyleSheet,
  Text,
  TouchableOpacity,
  View
} from 'react-native';
// import styles from '../Login/Design';
import { RNCamera } from 'react-native-camera';
import { uploadFile } from '../Chat/files';
import { ticket } from '../Chat/sendMessages';

export var uri = '';

export default class CameraAccess extends Component { 

  constructor(props) {
    super(props);
    this.state = {
      path: null,
      type: 'back'
    };
  }

  toggleFacing() {
    this.setState({
      type: this.state.type === 'back' ? 'front' : 'back',
    });
}

  render() {
    return (
      <View style={styles.container}>
        <RNCamera
            ref={ref => {
              this.camera = ref;
            }}
            style = {styles.preview}
            type={this.state.type}
            flashMode={RNCamera.Constants.FlashMode.auto}
            permissionDialogTitle={'Permission to use camera'}
            permissionDialogMessage={'We need your permission to use your camera phone'}
        />
        <View style={{flex: 0, flexDirection: 'row', justifyContent: 'space-around',}}>
        <TouchableOpacity style={styles.flipButton} onPress={this.toggleFacing.bind(this)}>
            <Text style={styles.flipText}> FLIP </Text>
        </TouchableOpacity>
        <TouchableOpacity
            onPress={this.takePicture.bind(this)}
            style = {[styles.capture, styles.picButton, { flex: 0.3, alignSelf: 'flex-end' }]} 
        >
            <Text style={styles.flipText}> SNAP </Text>
        </TouchableOpacity>
        </View>
      </View>
    );
  }

  takePicture = async function() {
    console.log(" ========================== takePicture =================================");
    if (this.camera) {
      const options = { quality: 0.5 };
      data = await this.camera.takePictureAsync(options);
      uploadFile(data.uri, ticket);
      };
    }
  };

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: 'black'
  },
  preview: {
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'center'
  },
  capture: {
    flex: 0,
    backgroundColor: '#fff',
    borderRadius: 5,
    padding: 15,
    paddingHorizontal: 20,
    alignSelf: 'center',
    margin: 20
  }
});
