<template>
  <main class="layout">
    <header class="hero">
      <h1>研究室向けイベント精算管理アプリ</h1>
      <p>まずはメンバー管理を先に実装し、コミット単位で開発履歴を残しています。</p>
    </header>

    <section class="card">
      <h2>メンバー管理</h2>
      <form class="form-grid" @submit.prevent="saveMember">
        <input v-model="form.name" placeholder="氏名" required />
        <input v-model="form.displayName" placeholder="表示名" />
        <input v-model="form.grade" placeholder="学年・属性 (例: M1)" />
        <label>
          <input v-model="form.isActive" type="checkbox" />
          有効
        </label>
        <button type="submit">{{ editingId ? '更新' : '追加' }}</button>
        <button v-if="editingId" type="button" class="secondary" @click="resetForm">キャンセル</button>
      </form>

      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

      <table>
        <thead>
          <tr>
            <th>名前</th>
            <th>表示名</th>
            <th>学年</th>
            <th>状態</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="member in members" :key="member.id">
            <td>{{ member.name }}</td>
            <td>{{ member.displayName || '-' }}</td>
            <td>{{ member.grade || '-' }}</td>
            <td>{{ member.isActive ? '有効' : '無効' }}</td>
            <td class="actions">
              <button class="small" @click="startEdit(member)">編集</button>
              <button class="small danger" @click="deleteMember(member.id)">削除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { memberApi } from './api'

const members = ref([])
const errorMessage = ref('')
const editingId = ref(null)

const form = reactive({
  name: '',
  displayName: '',
  grade: '',
  isActive: true
})

async function loadMembers() {
  errorMessage.value = ''
  try {
    members.value = await memberApi.list()
  } catch (error) {
    errorMessage.value = `メンバー取得に失敗: ${error.message}`
  }
}

async function saveMember() {
  errorMessage.value = ''
  const payload = {
    name: form.name,
    displayName: form.displayName,
    grade: form.grade,
    isActive: form.isActive
  }

  try {
    if (editingId.value) {
      await memberApi.update(editingId.value, payload)
    } else {
      await memberApi.create(payload)
    }
    resetForm()
    await loadMembers()
  } catch (error) {
    errorMessage.value = `保存に失敗: ${error.message}`
  }
}

function startEdit(member) {
  editingId.value = member.id
  form.name = member.name
  form.displayName = member.displayName || ''
  form.grade = member.grade || ''
  form.isActive = member.isActive
}

function resetForm() {
  editingId.value = null
  form.name = ''
  form.displayName = ''
  form.grade = ''
  form.isActive = true
}

async function deleteMember(id) {
  if (!confirm('このメンバーを削除しますか？')) return
  errorMessage.value = ''
  try {
    await memberApi.remove(id)
    if (editingId.value === id) {
      resetForm()
    }
    await loadMembers()
  } catch (error) {
    errorMessage.value = `削除に失敗: ${error.message}`
  }
}

onMounted(loadMembers)
</script>
