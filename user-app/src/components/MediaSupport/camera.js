import React, {Component} from 'react';
import {
  AppRegistry,
  Dimensions,
  StyleSheet,
  Text,
  Image,
  TouchableOpacity,
  TouchableHighlight,
  ToastAndroid,
  View
} from 'react-native';
// import styles from '../Login/Design';
import { RNCamera } from 'react-native-camera';
import { uploadFile } from '../Chat/files';
import { ticket } from '../Chat/sendMessages';
import Icon from 'react-native-vector-icons/MaterialIcons';

export var uri = '';

export default class CameraAccess extends Component { 

  constructor(props) {
    super(props);
    this.state = {
      path: null,
      type: 'back'
    };
  }

renderCamera() {
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
    >
    <View
          style={styles.bottomContainer}
    >
      <View style={{flex:1, alignItems: 'center', justifyContent: 'center'}}>
        <TouchableOpacity style={styles.flipButton} onPress={this.toggleFacing.bind(this)}>
          <Icon name="switch-camera" size={30} color="#FFF"/>
        </TouchableOpacity>
      </View>
      <View style={{flex:3, alignItems: 'center', justifyContent: 'center'}}>
        <TouchableOpacity
          style={ styles.capture }
          onPress={this.takePicture.bind(this)}
        >
          <Icon name="camera" size={30} color="#FFF"/>
        </TouchableOpacity>
      </View>
      <View style={{flex:1, alignItems: 'center', justifyContent: 'center'}}>
      </View>
    </View>
    </RNCamera>
  </View>
  );
}

renderImage() {
  return (
    <View style={styles.container}>
      <Image
        source={{ uri: this.state.path }}
        style={styles.preview}
      />
      <View
        style={styles.bottomContainer}
      >
        <View style={{flex:1, alignItems: 'center', justifyContent: 'center'}}>
          <TouchableOpacity style={styles.flipButton} onPress={this.switchCamera.bind(this)}>
            <Icon name="replay" size={30} color="#FFF"/>
          </TouchableOpacity>          
        </View>
        <View style={{flex:1, alignItems: 'center', justifyContent: 'center'}}>
          <TouchableOpacity
          style={ styles.capture }
          onPress={this.sendImage.bind(this)}
        >
          <Icon name="cloud-upload" size={30} color="#FFF"/>
          </TouchableOpacity>
        </View>
        <View style={{flex:1, alignItems: 'center', justifyContent: 'center'}}>
        </View>
      </View>
    </View>
  );
}

render() {
  return (
    <View style={styles.container}>
      {this.state.path ? this.renderImage() : this.renderCamera()}
    </View>
  );
}

  toggleFacing() {
    this.setState({
      type: this.state.type === 'back' ? 'front' : 'back',
    });
  }

  switchCamera() {
    //navigate back to the camera
    this.setState({ path: null});
  };

  takePicture = async function() {
    try {
      if (this.camera) {
        const options = { quality: 0.5 };
        data = await this.camera.takePictureAsync(options);
        this.setState({ file: data });
        this.setState({ path: data.uri });
      };
    } catch (err) {
      console.log('err: ', err);
    }
  };

  sendImage () {
    try{
      uploadFile(this.state.file, ticket);
      //navigate back to the camera
      this.setState({ path: null });  
    } catch (err) {
      console.log('err: ', err);
    }
  };
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#000',
  },
  bottomContainer: {
    flex: 0.2,
    flexDirection: 'row',
    // alignItems: 'center',
    justifyContent: 'space-between',
  },
  flipButton: {
    width: 50,
    height: 50,
    borderRadius: 30,
    borderWidth: 3,
    alignItems: 'center',
    justifyContent: 'center',
    borderColor: '#FFF',
  },
  preview: {
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'center',
    height: Dimensions.get('window').height,
    width: Dimensions.get('window').width
  },
  capture: {
    width: 60,
    height: 60,
    borderRadius: 35,
    borderWidth: 5,
    borderColor: '#FFF',
    justifyContent: 'center',
    alignItems: 'center',
    // marginBottom: 10,
  },
  cancel: {
    backgroundColor: 'transparent',
    fontWeight: '600',
    fontSize: 17,
    marginBottom: 20,
    left:20
  }
});
