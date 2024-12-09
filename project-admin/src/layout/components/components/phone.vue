<template>
  <el-dialog
    title="修改联系电话"
    :visible.sync="dialogFormVisible"
    width="568px"
    class="pwdCon"
    @close="handlePwdClose()"
  >
    <el-form ref="form" :model="form" label-width="85px" :rules="rules">
      <el-form-item label="新电话：" prop="newPhone">
        <el-input
          v-model="form.newPhone"
          type="text"
          placeholder="请输入电话号码"
        />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handlePwdClose()">
        取 消
      </el-button>
      <el-button type="primary" @click="handleSave()">
        保 存
      </el-button>
    </div>
  </el-dialog>
</template>
<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator'
import { Form as ElForm, Input } from 'element-ui'
// 接口
import {editEmployee } from '@/api/employee'
import { UserModule } from '@/store/modules/user';
@Component({
  name: 'Phone',
})
export default class extends Vue {
  @Prop() private dialogFormVisible!: any
  private validatePhone = (rule: any, value: any, callback: Function) => {
    const reg = /^(?:(?:\+|00)86)?1(?:(?:3[\d])|(?:4[5-79])|(?:5[0-35-9])|(?:6[5-7])|(?:7[0-8])|(?:8[\d])|(?:9[189]))\d{8}$/
    if (!value) {
      callback(new Error('请输入'))
    } else if (!reg.test(value)) {
      callback(new Error('号码格式错误'))
    } else {
      callback()
    }
  }
  rules = {
    newPhone: [{ validator: this.validatePhone, trigger: 'blur' }],
  }
  private form = {} as any
  private affirmPassword = ''
  handleSave() {
    (this.$refs.form as ElForm).validate(async (valid: boolean) => {
      if (valid) {
        const userInfo:any = UserModule.userInfo
        const params = {
          id: userInfo.id,  
          phone: this.form.newPhone,
        }
        await editEmployee(params)
        this.$emit('handleclose')
        ;(this.$refs.form as ElForm).resetFields()
      } else {
        return false
      }
    })
  }
  handlePwdClose() {
    (this.$refs.form as ElForm).resetFields()
    this.$emit('handleclose')
  }
}
</script>
<style lang="scss">
.navbar {
  .pwdCon {
    .el-dialog__body {
      padding-top: 60px;
      padding: 60px 100px 0;
    }
    .el-input__inner {
      padding: 0 12px;
    }
    .el-form-item {
      margin-bottom: 26px;
    }
    .el-form-item__label {
      text-align: left;
    }
    .el-dialog__footer {
      padding-top: 14px;
    }
  }
}
</style>
