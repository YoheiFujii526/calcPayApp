<template>
  <main class="layout">
    <header class="hero">
      <h1>研究室向けイベント精算管理アプリ</h1>
      <p>メンバー・イベント・支出・返済・精算を 1 画面で管理できます。</p>
    </header>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

    <section class="card">
      <h2>メンバー管理</h2>
      <form class="form-grid" @submit.prevent="saveMember">
        <input v-model="memberForm.name" placeholder="氏名" required />
        <input v-model="memberForm.displayName" placeholder="表示名" />
        <input v-model="memberForm.grade" placeholder="学年・属性" />
        <label><input v-model="memberForm.isActive" type="checkbox" /> 有効</label>
        <button type="submit">{{ editingMemberId ? '更新' : '追加' }}</button>
        <button v-if="editingMemberId" type="button" class="secondary" @click="resetMemberForm">キャンセル</button>
      </form>

      <table>
        <thead><tr><th>名前</th><th>表示名</th><th>学年</th><th>状態</th><th></th></tr></thead>
        <tbody>
          <tr v-for="member in members" :key="member.id">
            <td>{{ member.name }}</td>
            <td>{{ member.displayName || '-' }}</td>
            <td>{{ member.grade || '-' }}</td>
            <td>{{ member.isActive ? '有効' : '無効' }}</td>
            <td class="actions">
              <button class="small" @click="startMemberEdit(member)">編集</button>
              <button class="small danger" @click="deleteMember(member.id)">削除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="card">
      <h2>イベント管理</h2>
      <form class="form-grid" @submit.prevent="saveEvent">
        <input v-model="eventForm.title" placeholder="イベント名" required />
        <input v-model="eventForm.eventDate" type="date" required />
        <input v-model="eventForm.description" placeholder="説明" />
        <select v-model="eventForm.status">
          <option value="UNSETTLED">未精算</option>
          <option value="PARTIALLY_SETTLED">一部精算</option>
          <option value="SETTLED">精算完了</option>
        </select>
        <button type="submit">{{ editingEventId ? '更新' : '追加' }}</button>
        <button v-if="editingEventId" type="button" class="secondary" @click="resetEventForm">キャンセル</button>
      </form>

      <div class="event-list">
        <button
          v-for="event in events"
          :key="event.id"
          :class="['event-chip', { active: selectedEventId === event.id }]"
          @click="selectEvent(event.id)"
        >
          {{ event.title }} ({{ event.eventDate }})
        </button>
      </div>

      <table>
        <thead><tr><th>イベント</th><th>日付</th><th>参加人数</th><th>状態</th><th></th></tr></thead>
        <tbody>
          <tr v-for="event in events" :key="event.id">
            <td>{{ event.title }}</td>
            <td>{{ event.eventDate }}</td>
            <td>{{ event.participantCount }}</td>
            <td>{{ statusLabel(event.status) }}</td>
            <td class="actions">
              <button class="small" @click="startEventEdit(event)">編集</button>
              <button class="small danger" @click="deleteEvent(event.id)">削除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <section v-if="selectedEventId" class="card">
      <h2>イベント詳細 (ID: {{ selectedEventId }})</h2>

      <div class="grid2">
        <div>
          <h3>参加者</h3>
          <form class="inline" @submit.prevent="addParticipant">
            <select v-model.number="participantMemberId" required>
              <option :value="null">メンバーを選択</option>
              <option v-for="member in members" :key="member.id" :value="member.id">
                {{ member.displayName || member.name }}
              </option>
            </select>
            <button type="submit">追加</button>
          </form>
          <ul>
            <li v-for="participant in participants" :key="participant.id">
              {{ participant.displayName || participant.name }}
              <button class="small danger" @click="removeParticipant(participant.id)">削除</button>
            </li>
          </ul>
        </div>

        <div>
          <h3>支出登録</h3>
          <form class="form-grid" @submit.prevent="saveExpense">
            <input v-model="expenseForm.title" placeholder="支出名" required />
            <input v-model.number="expenseForm.amount" type="number" min="1" placeholder="金額" required />
            <input v-model="expenseForm.paidAt" type="date" required />
            <select v-model.number="expenseForm.paidByMemberId" required>
              <option :value="null">支払者</option>
              <option v-for="p in participants" :key="p.id" :value="p.id">{{ p.displayName || p.name }}</option>
            </select>
            <input v-model="expenseForm.memo" placeholder="メモ" />
            <div class="targets">
              <label v-for="p in participants" :key="`t-${p.id}`">
                <input type="checkbox" :value="p.id" v-model="expenseForm.targetMemberIds" />
                {{ p.displayName || p.name }}
              </label>
            </div>
            <button type="submit">支出追加</button>
          </form>

          <ul>
            <li v-for="expense in expenses" :key="expense.id">
              {{ expense.title }} / {{ expense.amount }}円 / {{ expense.paidByMemberName }}
              <button class="small danger" @click="deleteExpense(expense.id)">削除</button>
            </li>
          </ul>
        </div>
      </div>

      <div class="grid2">
        <div>
          <h3>返済登録</h3>
          <form class="form-grid" @submit.prevent="saveRepayment">
            <select v-model.number="repaymentForm.fromMemberId" required>
              <option :value="null">返済者</option>
              <option v-for="p in participants" :key="`f-${p.id}`" :value="p.id">{{ p.displayName || p.name }}</option>
            </select>
            <select v-model.number="repaymentForm.toMemberId" required>
              <option :value="null">受取者</option>
              <option v-for="p in participants" :key="`to-${p.id}`" :value="p.id">{{ p.displayName || p.name }}</option>
            </select>
            <input v-model.number="repaymentForm.amount" type="number" min="1" placeholder="金額" required />
            <input v-model="repaymentForm.repaidAt" type="date" required />
            <input v-model="repaymentForm.memo" placeholder="メモ" />
            <button type="submit">返済追加</button>
          </form>

          <ul>
            <li v-for="repayment in repayments" :key="repayment.id">
              {{ repayment.fromMemberName }} → {{ repayment.toMemberName }} : {{ repayment.amount }}円
              <button class="small danger" @click="deleteRepayment(repayment.id)">削除</button>
            </li>
          </ul>
        </div>

        <div>
          <h3>精算結果</h3>
          <button class="small" @click="loadDetail">再計算</button>
          <table>
            <thead><tr><th>メンバー</th><th>支払</th><th>負担</th><th>差額</th></tr></thead>
            <tbody>
              <tr v-for="row in settlement.members" :key="row.memberId">
                <td>{{ row.memberName }}</td>
                <td>{{ row.totalPaid }}</td>
                <td>{{ row.fairShare }}</td>
                <td :class="row.balance >= 0 ? 'plus' : 'minus'">{{ row.balance }}</td>
              </tr>
            </tbody>
          </table>
          <ul>
            <li v-for="(t, idx) in settlement.transfers" :key="idx">
              {{ nameById(t.fromMemberId) }} → {{ nameById(t.toMemberId) }} : {{ t.amount }}円
            </li>
          </ul>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import {
  eventApi,
  expenseApi,
  memberApi,
  participantApi,
  repaymentApi,
  settlementApi
} from './api'

const errorMessage = ref('')
const members = ref([])
const events = ref([])
const selectedEventId = ref(null)

const participants = ref([])
const expenses = ref([])
const repayments = ref([])
const settlement = ref({ members: [], transfers: [] })

const editingMemberId = ref(null)
const memberForm = reactive({ name: '', displayName: '', grade: '', isActive: true })

const editingEventId = ref(null)
const eventForm = reactive({ title: '', eventDate: '', description: '', status: 'UNSETTLED' })

const participantMemberId = ref(null)
const expenseForm = reactive({
  title: '', amount: 1, paidByMemberId: null, paidAt: '', memo: '', targetMemberIds: []
})
const repaymentForm = reactive({
  fromMemberId: null, toMemberId: null, amount: 1, repaidAt: '', memo: ''
})

function statusLabel(status) {
  if (status === 'SETTLED') return '精算完了'
  if (status === 'PARTIALLY_SETTLED') return '一部精算'
  return '未精算'
}

function nameById(id) {
  const p = participants.value.find((m) => m.id === id)
  if (p) return p.displayName || p.name
  const m = members.value.find((x) => x.id === id)
  return m ? (m.displayName || m.name) : `#${id}`
}

async function safeRun(fn, fallbackMessage) {
  errorMessage.value = ''
  try {
    await fn()
  } catch (error) {
    errorMessage.value = `${fallbackMessage}: ${error.message}`
  }
}

async function loadMembers() {
  members.value = await memberApi.list()
}

async function loadEvents() {
  events.value = await eventApi.list()
}

async function saveMember() {
  await safeRun(async () => {
    const payload = { ...memberForm }
    if (editingMemberId.value) await memberApi.update(editingMemberId.value, payload)
    else await memberApi.create(payload)
    resetMemberForm()
    await loadMembers()
  }, 'メンバー保存に失敗')
}

function startMemberEdit(member) {
  editingMemberId.value = member.id
  memberForm.name = member.name
  memberForm.displayName = member.displayName || ''
  memberForm.grade = member.grade || ''
  memberForm.isActive = member.isActive
}

function resetMemberForm() {
  editingMemberId.value = null
  memberForm.name = ''
  memberForm.displayName = ''
  memberForm.grade = ''
  memberForm.isActive = true
}

async function deleteMember(id) {
  if (!confirm('メンバーを削除しますか？')) return
  await safeRun(async () => {
    await memberApi.remove(id)
    if (editingMemberId.value === id) resetMemberForm()
    await loadMembers()
  }, 'メンバー削除に失敗')
}

async function saveEvent() {
  await safeRun(async () => {
    const payload = { ...eventForm }
    if (editingEventId.value) await eventApi.update(editingEventId.value, payload)
    else await eventApi.create(payload)
    resetEventForm()
    await loadEvents()
  }, 'イベント保存に失敗')
}

function startEventEdit(event) {
  editingEventId.value = event.id
  eventForm.title = event.title
  eventForm.eventDate = event.eventDate
  eventForm.description = event.description || ''
  eventForm.status = event.status
}

function resetEventForm() {
  editingEventId.value = null
  eventForm.title = ''
  eventForm.eventDate = ''
  eventForm.description = ''
  eventForm.status = 'UNSETTLED'
}

async function deleteEvent(id) {
  if (!confirm('イベントを削除しますか？')) return
  await safeRun(async () => {
    await eventApi.remove(id)
    if (selectedEventId.value === id) {
      selectedEventId.value = null
      participants.value = []
      expenses.value = []
      repayments.value = []
      settlement.value = { members: [], transfers: [] }
    }
    await loadEvents()
  }, 'イベント削除に失敗')
}

async function selectEvent(id) {
  selectedEventId.value = id
  await loadDetail()
}

async function loadDetail() {
  if (!selectedEventId.value) return
  await safeRun(async () => {
    const eventId = selectedEventId.value
    const [p, e, r, s] = await Promise.all([
      participantApi.list(eventId),
      expenseApi.list(eventId),
      repaymentApi.list(eventId),
      settlementApi.get(eventId)
    ])
    participants.value = p
    expenses.value = e
    repayments.value = r
    settlement.value = s
  }, 'イベント詳細取得に失敗')
}

async function addParticipant() {
  if (!selectedEventId.value || !participantMemberId.value) return
  await safeRun(async () => {
    await participantApi.add(selectedEventId.value, [participantMemberId.value])
    participantMemberId.value = null
    await loadDetail()
    await loadEvents()
  }, '参加者追加に失敗')
}

async function removeParticipant(memberId) {
  if (!selectedEventId.value) return
  await safeRun(async () => {
    await participantApi.remove(selectedEventId.value, memberId)
    await loadDetail()
    await loadEvents()
  }, '参加者削除に失敗')
}

async function saveExpense() {
  if (!selectedEventId.value) return
  await safeRun(async () => {
    await expenseApi.create(selectedEventId.value, { ...expenseForm })
    expenseForm.title = ''
    expenseForm.amount = 1
    expenseForm.paidByMemberId = null
    expenseForm.paidAt = ''
    expenseForm.memo = ''
    expenseForm.targetMemberIds = []
    await loadDetail()
  }, '支出登録に失敗')
}

async function deleteExpense(id) {
  await safeRun(async () => {
    await expenseApi.remove(id)
    await loadDetail()
  }, '支出削除に失敗')
}

async function saveRepayment() {
  if (!selectedEventId.value) return
  await safeRun(async () => {
    await repaymentApi.create(selectedEventId.value, { ...repaymentForm })
    repaymentForm.fromMemberId = null
    repaymentForm.toMemberId = null
    repaymentForm.amount = 1
    repaymentForm.repaidAt = ''
    repaymentForm.memo = ''
    await loadDetail()
  }, '返済登録に失敗')
}

async function deleteRepayment(id) {
  await safeRun(async () => {
    await repaymentApi.remove(id)
    await loadDetail()
  }, '返済削除に失敗')
}

onMounted(async () => {
  await safeRun(async () => {
    await Promise.all([loadMembers(), loadEvents()])
  }, '初期データ取得に失敗')
})
</script>
