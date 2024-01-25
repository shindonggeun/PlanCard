<template>
    <div id="main-container" class="container">
      <!-- session이 없을 때 -->
      <div id="join" v-if="!session">
  
  
        <div id="join-dialog" class="jumbotron vertical-center">
          <h1>Join a video session</h1>
  
          <div class="form-group">
            <p>
              <label>Participant</label>
              <!-- required 속성은 입력을 강제한다 -> 필수 입력 필드 -->
              <input v-model="myUserName" class="form-control" type="text" required />
            </p>
  
            <p>
              <label>Session</label>
              <input v-model="mySessionId" class="form-control" type="text" required />
            </p>
  
            <p class="text-center">
              <button class="btn btn-lg btn-success" @click="joinSession()">
                Join!
              </button>
            </p>
          </div>
        </div>
  
      </div>
  
      <!-- session이 있을 때 -->
      <div id="session" v-if="session">
        <div id="session-header">
          <h1 id="session-title">{{ mySessionId }}</h1>
          <input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession"
            value="Leave session" />
        </div>
        <div id="main-video" class="col-md-6">
          <user-video :stream-manager="mainStreamManager" />
        </div>
        <div id="video-container" class="col-md-6">
          <user-video :stream-manager="publisher" @click.native="updateMainVideoStreamManager(publisher)" />
          <user-video v-for="sub in subscribers" :key="sub.stream.connection.connectionId" :stream-manager="sub"
            @click.native="updateMainVideoStreamManager(sub)"/>
        </div>
      </div>
    </div>
  </template>
  
  
  
  
  <script>
    import axios from "axios";
    import { OpenVidu } from "openvidu-browser";
    import UserVideo from "./components/UserVideo";
  
    axios.defaults.headers.post["Content-Type"] = "application/json";
  
    const APPLICATION_SERVER_URL = process.env.NODE_ENV === 'production' ? '' : 'http://localhost:5000/';
  
    export default {
      name: "App",
  
      components: {
        UserVideo,
      },
  
      data() {
        return {
          // OpenVidu objects
          OV: undefined,
          session: undefined,
          mainStreamManager: undefined,  // 게시자 또는 구독자 중 한명이 표시되는 페이지의 기본 동영상 (메인 동영상)
          publisher: undefined,
          subscribers: [],  // 화상 통화에서 다른 사용자의 활성 스트림을 저장
  
          // Join form
          mySessionId: "SessionA",  // 세션 이름
          myUserName: "Participant" + Math.floor(Math.random() * 100),  // 참가자 이름
        };
      },
  
      methods: {
        // 사용자가 입장할 때마다 발생하는 메소드 -> JoinSession()
        // 세션 관련 메서드
        joinSession() {
          // --- 1) Get an OpenVidu object ---
          this.OV = new OpenVidu();  // OV 객체를 가져온다
  
          // --- 2) Init a session ---
          this.session = this.OV.initSession();  // OV 객체를 사용해서 session을 초기화하고, this.session에 할당한다.
  
          // --- 3) Specify the actions when events take place in the session --- 
  
          // On every new Stream received...  // 새로운 stream을 받으면, 해당 스트림에 대한 subscription을 생성하고, this.subscribers 배열에 추가한다.
          this.session.on("streamCreated", ({ stream }) => {
            const subscriber = this.session.subscribe(stream);
            this.subscribers.push(subscriber);  // subscriber를 this.subscribers 배열에 추가
          });
  
          // On every Stream destroyed...  // stream이 소멸되었을 때 발생하는 이벤트
          this.session.on("streamDestroyed", ({ stream }) => {
            const index = this.subscribers.indexOf(stream.streamManager, 0);  // 배열의 0번째 위치부터 stream.streamManager의 위치를 찾고, index 변수에 할당한다
            if (index >= 0) {  // index가 0 이상이면 == index가 있으면?
              this.subscribers.splice(index, 1);  // subscribers에서 index위치의 요소를 1개 삭제
            }
          });
  
          // On every asynchronous exception...  // 세션에서 발생하는 비동기 예외 이벤트에 대한 처리 -> 콘솔에 경고메시지를 출력한다.
          this.session.on("exception", ({ exception }) => {
            console.warn(exception);
          });
  
          // --- 4) Connect to the session with a valid user token ---
  
          // Get a token from the OpenVidu deployment
          this.getToken(this.mySessionId).then((token) => { // getToken 메서드로 세션에 참여하기 위한 토큰을 가져온다.
  
            // First param is the token. Second param can be retrieved by every user on event
            // 'streamCreated' (property Stream.connection.data), and will be appended to DOM as the user's nickname
            this.session.connect(token, { clientData: this.myUserName })  // 그 토큰으로 세션에 연결한다.
              .then(() => {
  
                // --- 5) Get your own camera stream with the desired properties ---
  
                // Init a publisher passing undefined as targetElement (we don't want OpenVidu to insert a video
                // element: we will manage it on our own) and with the desired properties
                let publisher = this.OV.initPublisher(undefined, {  // 사용자의 카메라 스트림을 초기화한다.
                  audioSource: undefined, // The source of audio. If undefined default microphone
                  videoSource: undefined, // The source of video. If undefined default webcam
                  publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
                  publishVideo: true, // Whether you want to start publishing with your video enabled or not
                  resolution: "640x480", // The resolution of your video
                  frameRate: 30, // The frame rate of your video
                  insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
                  mirror: false, // Whether to mirror your local video or not
                });
  
                // Set the main video in the page to display our webcam and store our Publisher
                // 초기화된 스트림을 할당한다.
                this.mainStreamManager = publisher;
                this.publisher = publisher;
  
                // --- 6) Publish your stream ---
  
                this.session.publish(this.publisher);  // 사용자의 세션을 발행한다.
              })
              .catch((error) => {
                console.log("There was an error connecting to the session:", error.code, error.message);
              });
          });
  
          window.addEventListener("beforeunload", this.leaveSession);
        },
  
        // 세션 나가기
        // 세션을 종료하기 위해 desconnect 메서드를 호출한다.
        leaveSession() {
          // --- 7) Leave the session by calling 'disconnect' method over the Session object ---
          if (this.session) this.session.disconnect();
  
          // Empty all properties...
          // 빈 값으로 만들기
          this.session = undefined;
          this.mainStreamManager = undefined;
          this.publisher = undefined;
          this.subscribers = [];
          this.OV = undefined;
  
          // Remove beforeunload listener
          // 이벤트 리스너를 제거
          window.removeEventListener("beforeunload", this.leaveSession);
        },
  
        updateMainVideoStreamManager(stream) {
          if (this.mainStreamManager === stream) return;
          this.mainStreamManager = stream;
        },
  
        /**
         * --------------------------------------------
         * GETTING A TOKEN FROM YOUR APPLICATION SERVER
         * --------------------------------------------
         * The methods below request the creation of a Session and a Token to
         * your application server. This keeps your OpenVidu deployment secure.
         *
         * In this sample code, there is no user control at all. Anybody could
         * access your application server endpoints! In a real production
         * environment, your application server must identify the user to allow
         * access to the endpoints.
         *
         * Visit https://docs.openvidu.io/en/stable/application-server to learn
         * more about the integration of OpenVidu in your application server.
         */
        async getToken(mySessionId) {
          const sessionId = await this.createSession(mySessionId);
          return await this.createToken(sessionId);
        },
  
        async createSession(sessionId) {
          const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions', { customSessionId: sessionId }, {
            headers: { 'Content-Type': 'application/json', },
          });
          return response.data; // The sessionId
        },
  
        async createToken(sessionId) {
          const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions/' + sessionId + '/connections', {}, {
            headers: { 'Content-Type': 'application/json', },
          });
          return response.data; // The token
        },
      },
    };
  </script>
  