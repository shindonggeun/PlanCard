<template>
    <div class="card p-fluid" id="box">
    <div class="container; card p-fluid" id="resettingBox">
      <h1 id="passwordResettingTitle">비밀번호 변경</h1>

      <form @submit.prevent="passwordResetting()" id="passwordResettingForm">
        
        <div class="box, card p-fluid" id="currentPasswordInput">
            <input type="password" id="currentPassword" v-model.trim="currentPassword" placeholder="기존 비밀번호">
        </div>

        <div class="box, card p-fluid" id="newPassword1Input">
            <input type="password" id="newPassword1" v-model.trim="newPassword1" placeholder="새로운 비밀번호">
        </div>

        <div class="box, card p-fluid" id="newPassword2Input">
            <input type="password" id="newPassword2" v-model.trim="newPassword2" placeholder="새로운 비밀번호 확인">
        </div>

        <div class="box, card p-fluid"  id="resettingSubmit" style=" text-align: center;">
            <input type="submit" value="변경하기">
        </div>
      </form>
    </div>
  </div>
</template>



<script setup>
  import { ref } from 'vue';
  import { useAccountsStore } from '@/stores/accountsStore.js';

  const accountsStore = useAccountsStore()

  const currentPassword = ref(null);
  const newPassword1 = ref(null);
  const newPassword2 = ref(null);

  const passwordResetting = function () {
      const info = {
        currentPassword: currentPassword.value,
        newPassword1: newPassword1.value,
        newPassword2: newPassword2.value,
      }
      accountsStore.passwordResetting(info)
  }
</script>



<style scoped>
  #box {
  display: flex;
  justify-content: center;
  }

  #resettingBox {
    background-color: #FFFFFF;
    border: 1px solid rgba(52, 152, 219, 0.5);
    width: 400px;
    height: 50%;
    padding: 40px;

    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
  }
  #passwordResettingTitle {
    color: #3498db;
    font-weight: bold;
  }
  #passwordResettingForm {
    display: flex;
    flex-direction: column;
    align-items: center;
  }


  #currentPassword, #newPassword1, #newPassword2 {
    height: 35px;
    width: 250px;
  }
  #currentPasswordInput, #newPassword1Input, #newPassword2Input {
    background-color: rgba(245, 245, 245, 0.1);
    width: 90%;
    display: flex;
    align-items: center;
    border: 1px solid rgba(52, 152, 219, 0.5);
    height: 40px;
    margin-bottom: 10px;
  }


  #resettingSubmit {
    width: 90%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid rgba(0, 0, 0, 0.1);
    background-color: #3498db;
    color: #FFFFFF;
    font-weight: bold;
    font-size: medium;
    height: 20px;
    padding: 20px;
  }

</style>