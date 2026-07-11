<template>
  <div class="home-container" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
    <button id="menu-btn" :class="{ active: isMenuOpen }" @click="toggleMenu">
      <span></span>
      <span></span>
      <span></span>
    </button>

    <div id="side-menu" :class="{ open: isMenuOpen }">
      <div class="menu-header">导航菜单</div>
      <div class="menu-item" :class="{ active: activePage === 'home' }" @click="showPage('home')">
        <div class="icon">🏠</div>
        <div>首页</div>
      </div>
      <div class="menu-item" :class="{ active: activePage === 'gym' }" @click="showPage('gym')">
        <div class="icon">💪</div>
        <div>健身训练</div>
      </div>
      <div class="menu-item" :class="{ active: activePage === 'diet' }" @click="showPage('diet')">
        <div class="icon">🍱</div>
        <div>饮食计划</div>
      </div>
      <div class="menu-divider"></div>
      <div class="menu-header">数据统计</div>
      <div class="menu-item" :class="{ active: activePage === 'stats' }" @click="showPage('stats')">
        <div class="icon">📊</div>
        <div>数据面板</div>
      </div>
      <div class="menu-item" :class="{ active: activePage === 'settings' }" @click="showPage('settings')">
        <div class="icon">⚙️</div>
        <div>设置</div>
      </div>
    </div>

    <div id="overlay" :class="{ show: isMenuOpen }" @click="toggleMenu"></div>

    <div id="home-page" class="page" :class="{ active: activePage === 'home' }">
      <div class="main-wrapper">
        <section id="header">
          <h1>欢迎来到我的网站</h1>
          <p>黄金比例 · 完整展示 · 纯粹内容</p>
        </section>

        <section id="content-card">
          <div id="card-text">
            <h2>最新公告</h2>
            <p>下面的三个板块是我主要发展的内容，有优质内容或者建议可以联系 <strong>QQ邮箱</strong>3134537596@qq.com</p>
            <div class="bullet-line"></div>
            <p>1.<a href="#" @click.prevent="showPage('gym')">健身训练</a>：个人的训练计划安排</p>
            <div class="bullet-line"></div>
            <p>2.<a href="#" @click.prevent="showPage('diet')">健身饮食</a>：饮食搭配</p>
            <div class="bullet-line"></div>
            <p v-if="!isLoggedIn">3.<a href="#" @click.prevent="openLoginModal">点击登录畅享更多功能</a></p>
            <p v-else>3. 欢迎回来，已解锁全部高级功能。</p>
          </div>
          <div id="card-image">
            <h3>品牌 Logo</h3>
            <img src="/logo.jpeg" alt="theking Logo">
          </div>
        </section>

        <section id="footer-area">
          <h2>更多空间等你探索</h2>
        </section>
      </div>
    </div>

    <div id="gym-page" class="page" :class="{ active: activePage === 'gym' }">
      <div class="gym-nav">
        <button class="gym-nav-btn" :class="{ active: activeGymTab === 'chest' }" @click="activeGymTab = 'chest'">胸部</button>
        <button class="gym-nav-btn" :class="{ active: activeGymTab === 'back' }" @click="activeGymTab = 'back'">背部</button>
        <button class="gym-nav-btn" :class="{ active: activeGymTab === 'shoulder' }" @click="activeGymTab = 'shoulder'">肩部</button>
        <button class="gym-nav-btn" :class="{ active: activeGymTab === 'legs' }" @click="activeGymTab = 'legs'">腿部</button>
        <button class="gym-nav-btn" :class="{ active: activeGymTab === 'arms' }" @click="activeGymTab = 'arms'">手臂</button>
      </div>

      <div class="gym-tab-content">
        <div v-for="(blocks, sectionId) in gymData" :key="sectionId" v-show="activeGymTab === sectionId" class="gym-section active">
          
          <div v-if="sectionId === 'back'" class="workout-block">
            <h2 class="block-title">常规项目</h2>
            <ul class="exercise-list">
              <li v-for="(ex, index) in blocks.normal" :key="index" class="exercise-item" :class="{ completed: completedExercises[sectionId + '_normal_' + index] }" @click="toggleCompleted(sectionId + '_normal_' + index, $event)">
                <span class="action-name" @click.stop="openActionModal(ex.name)">{{ ex.name }}</span>
                <input type="text" class="weight-input" v-model="exerciseWeights[getWeightKey(sectionId, ex.name)]" @input="saveWeight(getWeightKey(sectionId, ex.name))" @blur="trimWeight(getWeightKey(sectionId, ex.name))" @keydown.enter="blurInput($event)" :placeholder="ex.placeholder">
              </li>
            </ul>
            <div class="workout-block" style="margin-top:20px; padding:0; border:none;">
              <h2 class="block-title">变式项目</h2>
              <ul class="exercise-list">
                <li v-for="(ex, index) in blocks.variant" :key="index" class="exercise-item" :class="{ completed: completedExercises[sectionId + '_variant_' + index] }" @click="toggleCompleted(sectionId + '_variant_' + index, $event)">
                  <span class="action-name" @click.stop="openActionModal(ex.name)">{{ ex.name }}</span>
                  <input type="text" class="weight-input" v-model="exerciseWeights[getWeightKey(sectionId, ex.name)]" @input="saveWeight(getWeightKey(sectionId, ex.name))" @blur="trimWeight(getWeightKey(sectionId, ex.name))" @keydown.enter="blurInput($event)" :placeholder="ex.placeholder">
                </li>
              </ul>
            </div>
            <div class="workout-block" style="margin-top:20px; padding:15px 20px; border:1px solid #333;">
              <h2 class="block-title">对抗肌训练</h2>
              <p class="note">参考胸部训练，如果采用对抗肌训练不能两天连着练。</p>
            </div>
          </div>

          <div v-else>
            <div v-for="(block, bIndex) in blocks" :key="bIndex" class="workout-block">
              <h2 class="block-title">{{ block.title }}</h2>
              <ul class="exercise-list">
                <li v-for="(ex, eIndex) in block.list" :key="eIndex" class="exercise-item" :class="{ completed: completedExercises[sectionId + '_' + bIndex + '_' + eIndex] }" @click="toggleCompleted(sectionId + '_' + bIndex + '_' + eIndex, $event)">
                  <span class="action-name" @click.stop="openActionModal(ex.name)">{{ ex.name }}</span>
                  <input type="text" class="weight-input" v-model="exerciseWeights[getWeightKey(sectionId, ex.name)]" @input="saveWeight(getWeightKey(sectionId, ex.name))" @blur="trimWeight(getWeightKey(sectionId, ex.name))" @keydown.enter="blurInput($event)" :placeholder="ex.placeholder">
                </li>
              </ul>
            </div>
          </div>

        </div>
      </div>
    </div>

    <div id="diet-page" class="page" :class="{ active: activePage === 'diet' }">
      <div class="diet-nav">
        <button class="diet-nav-btn training" :class="{ active: activeDietTab === 'training' }" @click="activeDietTab = 'training'">训练日</button>
        <button class="diet-nav-btn rest" :class="{ active: activeDietTab === 'rest' }" @click="activeDietTab = 'rest'">休息日</button>
      </div>

      <div id="training" class="diet-section" :class="{ active: activeDietTab === 'training' }">
        <div class="table-title-row">
          <h1 style="font-size: 22px; color: #2e7d32; margin: 0;">🏋️ 训练日饮食计划</h1>
          <div class="diet-actions">
            <button class="btn-outline-small" @click="isEditScheduleModalOpen = true">安排排期</button>
            <button class="btn-outline-small" @click="openDietEntryModal">添加/导入</button>
          </div>
        </div>

        <div class="diet-table-wrapper">
          <table class="diet-table">
            <thead>
              <tr>
                <th>日期</th>
                <th>训练部位</th>
                <th>11:30 早午餐</th>
                <th>16:30 练前</th>
                <th>19:00 练后</th>
                <th>21:00 晚餐</th>
                <th style="text-align:center">总热量</th>
                <th style="text-align:center">蛋白质</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in trainingDays" :key="idx">
                <td><strong>{{ row.day }}</strong></td>
                <td><span class="type-badge" :class="getBadgeClass(row.part)">{{ row.part }}</span></td>
                <td>{{ row.brunch }}</td>
                <td :class="{ dash: row.pre === '—' || !row.pre }">{{ row.pre || '—' }}</td>
                <td :class="{ dash: row.post === '—' || !row.post }">{{ row.post || '—' }}</td>
                <td>{{ row.dinner }}</td>
                <td class="data-highlight data-cal" style="text-align:center">{{ row.cal }}</td>
                <td class="data-highlight data-protein" style="text-align:center">{{ row.protein }}</td>
              </tr>
              <tr v-if="trainingDays.length === 0">
                <td colspan="8" style="text-align: center; padding: 30px; color: #888;">暂无训练日安排，请点击右上角“安排排期”</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div id="rest" class="diet-section" :class="{ active: activeDietTab === 'rest' }">
        <div class="table-title-row">
          <h1 style="font-size: 22px; color: #1565c0; margin: 0;">🧘 休息日饮食计划</h1>
          <div class="diet-actions">
            <button class="btn-outline-small" @click="isEditScheduleModalOpen = true">安排排期</button>
            <button class="btn-outline-small" @click="openDietEntryModal">添加/导入</button>
          </div>
        </div>

        <div class="rest-table-wrapper">
          <table class="rest-table">
            <thead>
              <tr>
                <th>日期</th>
                <th>11:30 早午餐</th>
                <th>21:00 晚餐</th>
                <th style="text-align:center">总热量</th>
                <th style="text-align:center">蛋白质</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in restDays" :key="idx">
                <td><strong>{{ row.day }}</strong><br><span class="type-badge badge-rest" style="margin-top:6px">休息日</span></td>
                <td>
                  <span class="section-label">早午餐</span>
                  <span class="section-content">{{ row.brunch }}</span>
                </td>
                <td>
                  <span class="section-label">晚餐</span>
                  <span class="section-content">{{ row.dinner }}</span>
                </td>
                <td class="macro-cell data-cal">{{ row.cal }}</td>
                <td class="macro-cell data-protein">{{ row.protein }}</td>
              </tr>
              <tr v-if="restDays.length === 0">
                <td colspan="5" style="text-align: center; padding: 30px; color: #888;">暂无休息日安排，请点击右上角“安排排期”</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="diet-section active" style="padding-top: 0;">
        <div class="tip-card deficit-card">
          <h3>🔥 今日热量与缺口分析 ({{ todayName }})</h3>
          
          <div v-if="!isLoggedIn || !calculatedStats.tdee" style="color: #888; font-size: 14px; margin-top: 10px;">
            请先登录并在“设置”中填写身高体重，并确保在“数据统计”面板选择今日训练模板，以获取准确热量缺口。
          </div>
          <div v-else>
            <div class="deficit-info">
              <div class="d-item">
                <div class="d-label">今日总消耗 (TDEE)</div>
                <div class="d-value text-accent">{{ calculatedStats.tdee }} <span class="unit">kcal</span></div>
              </div>
              <div class="d-operator">-</div>
              <div class="d-item">
                <div class="d-label">今日计划摄入</div>
                <div class="d-value">{{ todayDietIntake }} <span class="unit">kcal</span></div>
              </div>
              <div class="d-operator">=</div>
              <div class="d-item">
                <div class="d-label">今日热量缺口</div>
                <div class="d-value" :class="{'text-green': todayDeficit > 0, 'text-red': todayDeficit <= 0}">{{ todayDeficit > 0 ? todayDeficit : 0 }} <span class="unit">kcal</span></div>
              </div>
            </div>
            <p class="deficit-note" v-if="todayDeficit > 0">保持这个缺口，稳步向目标迈进！记得多喝水保证睡眠。</p>
            <p class="deficit-note text-red" v-else>注意：今日摄入可能超过或等于消耗，建议增加运动量或微调饮食。</p>
          </div>
        </div>
      </div>
    </div>

    <div id="stats-page" class="page" :class="{ active: activePage === 'stats' }">
      <div class="stats-wrapper">
        <h2 class="stats-page-title">数据面板</h2>
        
        <div class="stats-card">
          <div class="card-header">
            <h3>🩺 身体数据分析</h3>
            <button class="go-settings-btn" @click="showPage('settings')">修改身高体重</button>
          </div>
          <div v-if="!isLoggedIn || !userProfile.height || !userProfile.weight" class="empty-state">
            <p>请在“设置”页面完善身高体重以获取分析数据</p>
          </div>
          <div v-else class="analysis-grid">
            <div class="analysis-item">
              <div class="data-value">{{ calculatedStats.bmi }}</div>
              <div class="data-label">BMI (<span :class="['status-text', calculatedStats.bmiClass]">{{ calculatedStats.bmiStatus }}</span>)</div>
            </div>
            <div class="analysis-item">
              <div class="data-value">{{ calculatedStats.bmr }} <span class="unit">kcal</span></div>
              <div class="data-label">基础代谢率 (BMR)</div>
            </div>
            <div class="analysis-item">
              <div class="data-value text-accent">{{ calculatedStats.tdee }} <span class="unit">kcal</span></div>
              <div class="data-label">今日总消耗 (TDEE)</div>
            </div>
          </div>
          <p v-if="isLoggedIn && userProfile.height" class="stats-note">* 今日 TDEE = BMR × 1.2 + 今日所选模板消耗 (若未选择视为休息日)</p>
        </div>

        <!-- AI 体重预测 -->
        <div class="stats-card">
          <div class="card-header">
            <h3>🤖 AI 体重趋势预测</h3>
            <button class="btn-primary-small" @click="loadWeightPrediction">刷新预测</button>
          </div>
          <div v-if="weightPrediction.loading" class="empty-state">
            <p>AI 分析中...</p>
          </div>
          <div v-else-if="weightPrediction.error" class="empty-state">
            <p>{{ weightPrediction.error }}</p>
          </div>
          <div v-else-if="weightPrediction.data" class="prediction-result">
            <div class="prediction-main">
              <div class="prediction-item">
                <div class="prediction-value">{{ weightPrediction.data.currentWeight }}</div>
                <div class="prediction-label">当前体重 (kg)</div>
              </div>
              <div class="prediction-arrow">→</div>
              <div class="prediction-item">
                <div class="prediction-value" :class="weightPrediction.data.trend">{{ weightPrediction.data.predictedWeight }}</div>
                <div class="prediction-label">预测明天 (kg)</div>
              </div>
            </div>
            <div class="prediction-change" :class="weightPrediction.data.trend">
              {{ weightPrediction.data.trend === 'up' ? '↑' : '↓' }} 
              {{ Math.abs(weightPrediction.data.change) }} kg
            </div>
            <p class="prediction-hint">基于 {{ weightHistory.length }} 天历史数据，由 TensorFlow LSTM 模型分析</p>
          </div>
          <div v-else class="empty-state">
            <p>记录体重后，AI 将为您预测趋势</p>
          </div>
        </div>

                <!-- 智能热量计算 -->
        <div class="stats-card">
          <div class="card-header">
            <h3>🔥 智能热量计算</h3>
            <button class="btn-primary-small" @click="calculateCalories">计算</button>
          </div>
          <div v-if="!isLoggedIn" class="empty-state">
            <p>请先登录使用 AI 热量计算</p>
          </div>
          <div v-else>
            <div class="calorie-form">
              <div class="form-row">
                <div class="form-item">
                  <label>体重 (kg)</label>
                  <input type="number" v-model="calorieForm.weight" placeholder="70">
                </div>
                <div class="form-item">
                  <label>身高 (cm)</label>
                  <input type="number" v-model="calorieForm.height" placeholder="175">
                </div>
              </div>
              <div class="form-row">
                <div class="form-item">
                  <label>年龄</label>
                  <input type="number" v-model="calorieForm.age" placeholder="25">
                </div>
                <div class="form-item">
                  <label>性别</label>
                  <select v-model="calorieForm.gender">
                    <option value="male">男</option>
                    <option value="female">女</option>
                  </select>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item" style="width: 100%;">
                  <label>活动量</label>
                  <select v-model="calorieForm.activity">
                    <option value="sedentary">久坐不动</option>
                    <option value="light">轻度活动</option>
                    <option value="moderate">中度活动</option>
                    <option value="active">高度活动</option>
                    <option value="very_active">极度活动</option>
                  </select>
                </div>
              </div>
            </div>
            
            <div v-if="calorieResult.loading" class="empty-state">
              <p>AI 计算中...</p>
            </div>
            <div v-else-if="calorieResult.data" class="calorie-result">
              <div class="result-grid">
                <div class="result-item">
                  <div class="result-value">{{ calorieResult.data.bmr }}</div>
                  <div class="result-label">基础代谢 (BMR)</div>
                </div>
                <div class="result-item">
                  <div class="result-value text-accent">{{ calorieResult.data.tdee }}</div>
                  <div class="result-label">每日总消耗 (TDEE)</div>
                </div>
                <div class="result-item">
                  <div class="result-value text-green">{{ calorieResult.data.recommendation }}</div>
                  <div class="result-label">减脂建议摄入</div>
                </div>
              </div>
            </div>
          </div>
        </div>

<div class="stats-card">
          <div class="card-header">
            <h3>🏋️ 我的训练模板库</h3>
            <button class="btn-primary-small" @click="openAddWorkoutModal">+ 添加</button>
          </div>
          
          <div v-if="workoutLogs.length === 0" class="empty-state">
            <p>暂无训练模板，点击右上角添加您的日常训练组合</p>
          </div>
          
          <div class="routine-list">
            <div v-for="log in workoutLogs" :key="log.id" class="routine-card" :class="{ 'selected-card': selectedWorkoutId === log.id }">
              <div class="routine-header">
                <h4>{{ log.partName }} 训练</h4>
                <div class="routine-actions">
                  <button class="select-btn" :class="{ active: selectedWorkoutId === log.id }" @click="selectWorkout(log.id)">
                    {{ selectedWorkoutId === log.id ? '✅ 今日已选' : '选择此模板' }}
                  </button>
                  <button class="delete-btn" @click="deleteLog(log.id)">&times;</button>
                </div>
              </div>
              <div class="routine-body">
                <div v-for="(ex, index) in log.exercises" :key="index" class="routine-ex-item">
                  <span class="ex-name">{{ ex.name }}</span>
                  <span class="ex-data">{{ ex.weight }}kg × {{ ex.sets }}组</span>
                </div>
              </div>
              <div class="routine-footer">
                共计 {{ log.totalSets }} 组 (约消耗 {{ log.totalSets * 15 }} kcal)
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>

    <div id="settings-page" class="page" :class="{ active: activePage === 'settings' }">
      <div class="settings-wrapper">
        <h2 class="settings-title">设置</h2>

        <div class="settings-group">
          <div class="settings-item profile-item">
            <div class="profile-avatar">🤴</div>
            <div class="profile-info">
              <div class="profile-name">{{ isLoggedIn ? (loginForm.account || 'The King') : '未登录' }}</div>
              <div class="profile-email">{{ isLoggedIn ? '开启你的专属数字空间' : '登录后体验完整功能' }}</div>
            </div>
            <button v-if="!isLoggedIn" class="btn-primary-small" @click="openLoginModal">登录</button>
          </div>
        </div>

        <div class="settings-group" v-if="isLoggedIn">
          <div class="settings-item profile-input-item">
            <label>年龄</label>
            <input type="number" v-model="userProfile.age" @blur="saveProfile" placeholder="输入年龄">
          </div>
          <div class="settings-item profile-input-item">
            <label>身高 (cm)</label>
            <input type="number" v-model="userProfile.height" @blur="saveProfile" placeholder="输入身高">
          </div>
          <div class="settings-item profile-input-item">
            <label>体重 (kg)</label>
            <input type="number" v-model="userProfile.weight" @blur="saveProfile" placeholder="输入体重">
          </div>
          <div class="settings-item profile-input-item">
            <label>性别</label>
            <select v-model="userProfile.gender" @change="saveProfile">
              <option value="male">男</option>
              <option value="female">女</option>
            </select>
          </div>
        </div>

        <div class="settings-group">
          <div class="settings-item">
            <div class="item-left">
              <span class="icon">🔔</span>
              <span>训练提醒</span>
            </div>
            <label class="apple-switch">
              <input type="checkbox" v-model="settingsConfig.notifications">
              <span class="slider"></span>
            </label>
          </div>
          <div class="settings-item">
            <div class="item-left">
              <span class="icon">🌙</span>
              <span>深色模式</span>
            </div>
            <label class="apple-switch">
              <input type="checkbox" v-model="settingsConfig.darkMode" @change="toggleDarkMode">
              <span class="slider"></span>
            </label>
          </div>
        </div>

        <div class="settings-group">
          <div class="settings-item clickable" @click="clearCache">
            <div class="item-left">
              <span class="icon">🗑️</span>
              <span>清除本地缓存</span>
            </div>
            <span class="arrow">›</span>
          </div>
          <div class="settings-item clickable" @click="handleLogout" v-if="isLoggedIn">
            <div class="item-left text-danger" style="justify-content: center; width: 100%;">
              <span>退出登录</span>
            </div>
          </div>
        </div>

      </div>
    </div>

    <div class="login-modal-overlay" :class="{ show: isEditScheduleModalOpen }" @click="isEditScheduleModalOpen = false">
      <div class="add-workout-modal" style="max-width: 400px;" @click.stop>
        <div class="modal-header">
          <h3>安排一周训练排期</h3>
          <button class="close-btn" @click="isEditScheduleModalOpen = false">&times;</button>
        </div>
        <div class="modal-body">
          <p style="font-size: 13px; color: #888; margin-bottom: 15px;">设置每一天的状态，数据将自动同步到饮食表。</p>
          <div v-for="day in ['周一','周二','周三','周四','周五','周六','周日']" :key="day" class="schedule-row">
            <span class="day-label">{{ day }}</span>
            <select v-model="userDietPlan[day].isRest" class="mini-select">
              <option :value="false">训练日</option>
              <option :value="true">休息日</option>
            </select>
            <input v-if="!userDietPlan[day].isRest" type="text" v-model="userDietPlan[day].part" class="mini-input part-input" placeholder="输入部位(如:练腿)">
            <span v-else style="width: 100px; color: #aaa; font-size: 13px; text-align: center;">完全休息</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-primary" @click="saveDietSchedule">保存安排</button>
        </div>
      </div>
    </div>

    <div class="login-modal-overlay" :class="{ show: isDietEntryModalOpen }" @click="isDietEntryModalOpen = false">
      <div class="add-workout-modal" style="max-width: 500px;" @click.stop>
        <div class="modal-header">
          <h3>管理饮食计划</h3>
          <button class="close-btn" @click="isDietEntryModalOpen = false">&times;</button>
        </div>
        
        <div class="modal-body">
          <p style="font-size: 13px; color: #ff9800; margin-bottom: 15px; font-weight: bold; background: rgba(255, 152, 0, 0.1); padding: 10px; border-radius: 8px;">
            💡 Tip：本网站不提供食物热量计算，只提供表格供您使用。
          </p>
          
          <div class="auth-tabs" style="margin-bottom: 15px;">
            <div class="auth-tab" :class="{ active: dietEntryTab === 'manual' }" @click="dietEntryTab = 'manual'">手动录入</div>
            <div class="auth-tab" :class="{ active: dietEntryTab === 'file' }" @click="dietEntryTab = 'file'">智能导入</div>
          </div>

          <div v-if="dietEntryTab === 'manual'">
            <div class="form-group">
              <label>选择星期</label>
              <select v-model="manualDietForm.day" class="form-select">
                <option v-for="d in ['周一','周二','周三','周四','周五','周六','周日']" :key="d" :value="d">{{ d }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>11:30 早午餐 / 早餐</label>
              <input type="text" v-model="manualDietForm.brunch" class="form-select" placeholder="例如: 鸡腿×2 + 米饭200g">
            </div>
            <div class="form-group" style="display: flex; gap: 10px;">
              <div style="flex: 1;">
                <label>16:30 练前 (可选)</label>
                <input type="text" v-model="manualDietForm.pre" class="form-select" placeholder="例如: 碱水包">
              </div>
              <div style="flex: 1;">
                <label>19:00 练后 (可选)</label>
                <input type="text" v-model="manualDietForm.post" class="form-select" placeholder="例如: 蛋白粉">
              </div>
            </div>
            <div class="form-group">
              <label>21:00 晚餐</label>
              <input type="text" v-model="manualDietForm.dinner" class="form-select" placeholder="例如: 鸡腿 + 蔬菜">
            </div>
            <div class="form-group" style="display: flex; gap: 10px;">
              <div style="flex: 1;">
                <label>总热量 (kcal)</label>
                <input type="number" v-model="manualDietForm.cal" class="form-select" placeholder="如: 1800">
              </div>
              <div style="flex: 1;">
                <label>蛋白质 (g)</label>
                <input type="text" v-model="manualDietForm.protein" class="form-select" placeholder="如: 130g">
              </div>
            </div>
            <button class="btn-primary" @click="saveManualDiet">保存至 {{ manualDietForm.day }}</button>
          </div>

          <div v-if="dietEntryTab === 'file'">
            <p style="font-size: 13px; color: var(--text-sub); margin-bottom: 15px; line-height: 1.6;">
              自动提取功能：你可以上传任意包含 <b>“周一~周日”</b>、<b>“早午餐/早餐”</b>、<b>“晚餐”</b>、<b>“热量”</b> 等关键字的 TXT/MD/JSON 文本文件，或直接在下方粘贴文本，系统将智能提取对应数据。
            </p>
            <button class="btn-outline-small" style="width: 100%; padding: 12px; margin-bottom: 15px; font-size: 14px;" @click="triggerDietFile">
              📁 点击选择文件 (txt, md, json等)
            </button>
            <input type="file" ref="dietFileInput" style="display:none" @change="handleSmartImport" accept=".txt,.json,.md,.csv">
            
            <textarea v-model="smartParseText" class="form-select" rows="6" placeholder="或者直接在此粘贴您的饮食计划内容..." style="resize: vertical; font-family: monospace; font-size: 13px;"></textarea>
            
            <button class="btn-primary" style="margin-top: 15px;" @click="parseTextToDiet">🤖 提取并保存</button>
          </div>
          
        </div>
      </div>
    </div>

    <div class="login-modal-overlay" :class="{ show: isAddWorkoutOpen }" @click="closeAddWorkoutModal">
      <div class="add-workout-modal" @click.stop>
        <div class="modal-header">
          <h3>创建训练模板</h3>
          <button class="close-btn" @click="closeAddWorkoutModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>目标部位 (可多选)</label>
            <div class="part-checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" value="chest" v-model="newWorkoutForm.parts" @change="updateAvailableExercises"> 胸部
              </label>
              <label class="checkbox-label">
                <input type="checkbox" value="back" v-model="newWorkoutForm.parts" @change="updateAvailableExercises"> 背部
              </label>
              <label class="checkbox-label">
                <input type="checkbox" value="shoulder" v-model="newWorkoutForm.parts" @change="updateAvailableExercises"> 肩部
              </label>
              <label class="checkbox-label">
                <input type="checkbox" value="legs" v-model="newWorkoutForm.parts" @change="updateAvailableExercises"> 腿部
              </label>
              <label class="checkbox-label">
                <input type="checkbox" value="arms" v-model="newWorkoutForm.parts" @change="updateAvailableExercises"> 手臂
              </label>
            </div>
          </div>
          
          <div class="form-group">
            <label style="margin-bottom: 10px; display: block;">选择动作 & 录入数据</label>
            <div class="exercise-select-list">
              <div v-for="(ex, idx) in availableExercises" :key="idx" class="exercise-select-item">
                <label class="checkbox-label">
                  <input type="checkbox" v-model="ex.selected">
                  <span class="ex-label-name">{{ ex.name }}</span>
                </label>
                <div v-if="ex.selected" class="ex-inputs">
                  <input type="number" v-model="ex.weight" placeholder="重量" class="mini-input"> <span class="unit">kg</span>
                  <input type="number" v-model="ex.sets" placeholder="组数" class="mini-input"> <span class="unit">组</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-primary" @click="saveWorkoutLog">保存模板</button>
        </div>
      </div>
    </div>

    <div class="action-modal-overlay" :class="{ show: isModalOpen }" @click="closeActionModal">
      <div class="action-modal" @click.stop>
        <div class="action-modal-header">
          <h3>{{ modalActionName }}</h3>
          <div class="target-muscle">{{ modalData ? modalData.muscle : '目标肌群' }}</div>
          <button class="action-modal-close" @click="closeActionModal">&times;</button>
        </div>
        <div class="action-modal-body">
          <div class="action-video-container">
            <video v-if="modalData && modalData.video" :src="modalData.video" loop muted playsinline autoplay></video>
            <div v-else class="action-video-placeholder">
              <div class="placeholder-icon">&#127947;</div>
              <div>{{ modalData ? '此动作暂无视频演示' : '暂无此动作的详细数据' }}</div>
              <div style="font-size:12px;color:#555;margin-top:4px">{{ modalData ? '文字步骤如下' : '欢迎补充动作说明' }}</div>
            </div>
          </div>
          
          <div v-if="modalData">
            <div class="action-steps">
              <h4>动作步骤</h4>
              <ol>
                <li v-for="(step, sIdx) in modalData.steps" :key="sIdx">{{ step }}</li>
              </ol>
            </div>
            <div class="section-divider"></div>
            <div class="action-tips">
              <h4>关键要点</h4>
              <ul>
                <li v-for="(tip, tIdx) in modalData.tips" :key="tIdx" class="tip-li">{{ tip }}</li>
              </ul>
            </div>
            <div v-if="modalData.mistakes && modalData.mistakes.length > 0">
              <div class="section-divider"></div>
              <div class="action-mistakes">
                <h4>常见错误</h4>
                <ul>
                  <li v-for="(mis, mIdx) in modalData.mistakes" :key="mIdx" class="mistake-li">{{ mis }}</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="login-modal-overlay" :class="{ show: isLoginModalOpen }" @click="closeLoginModal">
      <div class="login-modal" @click.stop>
        <button class="login-modal-close" @click="closeLoginModal">&times;</button>

        <div class="auth-tabs">
          <div class="auth-tab" :class="{ active: authTab === 'login' }" @click="authTab = 'login'">登录</div>
          <div class="auth-tab" :class="{ active: authTab === 'register' }" @click="authTab = 'register'; refreshCaptcha()">注册</div>
          <div class="auth-tab" :class="{ active: authTab === 'forgot' }" @click="authTab = 'forgot'">忘记密码</div>
        </div>

        <div v-if="authTab === 'login'" class="auth-panel">
          <div class="login-header">
            <h2>账号登录</h2>
            <p>进入你的专属数字空间</p>
          </div>
          <div class="login-form">
            <div class="input-wrapper">
              <input type="text" v-model="loginForm.account" placeholder="账号" @keydown.enter="blurInput($event)">
            </div>
            <div class="input-wrapper">
              <input type="password" v-model="loginForm.password" placeholder="密码" @keydown.enter="handleLogin">
            </div>
            <button class="btn-login" @click="handleLogin" :disabled="isLoading">
              {{ isLoading ? '登录中...' : '登 录' }}
            </button>
          </div>
        </div>

        <div v-if="authTab === 'register'" class="auth-panel">
          <div class="login-header">
            <h2>新用户注册</h2>
            <p>创建账号开启健身之旅</p>
          </div>
          <div class="login-form">
            <div class="input-wrapper">
              <input type="text" v-model="registerForm.account" placeholder="账号（唯一）" @keydown.enter="blurInput($event)">
            </div>
            <div class="input-wrapper">
              <input type="password" v-model="registerForm.password" placeholder="密码（至少6位）" @keydown.enter="blurInput($event)">
            </div>
            <div class="input-wrapper">
              <input type="password" v-model="registerForm.confirmPassword" placeholder="确认密码" @keydown.enter="blurInput($event)">
            </div>
            <div class="input-wrapper">
              <input type="email" v-model="registerForm.email" placeholder="邮箱（用于找回密码）" @keydown.enter="blurInput($event)">
            </div>
            <div class="input-wrapper captcha-row">
              <input type="text" v-model="registerForm.captchaCode" placeholder="图形验证码" class="captcha-input" @keydown.enter="handleRegister">
              <img v-if="captchaImage" :src="captchaImage" class="captcha-img" @click="refreshCaptcha" title="点击刷新">
              <div v-else class="captcha-placeholder" @click="refreshCaptcha">点击获取</div>
            </div>
            <button class="btn-login" @click="handleRegister" :disabled="isLoading">
              {{ isLoading ? '注册中...' : '立即注册' }}
            </button>
          </div>
        </div>

        <div v-if="authTab === 'forgot'" class="auth-panel">
          <div class="login-header">
            <h2>重置密码</h2>
            <p>验证邮箱后设置新密码</p>
          </div>
          <div class="login-form">
            <div class="input-wrapper">
              <input type="text" v-model="forgotForm.account" placeholder="账号" @keydown.enter="blurInput($event)">
            </div>
            <div class="input-wrapper">
              <input type="email" v-model="forgotForm.email" placeholder="绑定邮箱" @keydown.enter="blurInput($event)">
            </div>
            <div class="input-wrapper code-row">
              <input type="text" v-model="forgotForm.code" placeholder="邮箱验证码" class="code-input" @keydown.enter="blurInput($event)">
              <button class="btn-send-code" @click="sendForgotCode" :disabled="codeCountdown > 0">
                {{ codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}
              </button>
            </div>
            <div class="input-wrapper">
              <input type="password" v-model="forgotForm.newPassword" placeholder="新密码（至少6位）" @keydown.enter="blurInput($event)">
            </div>
            <div class="input-wrapper">
              <input type="password" v-model="forgotForm.confirmPassword" placeholder="确认新密码" @keydown.enter="handleForgot">
            </div>
            <button class="btn-login" @click="handleForgot" :disabled="isLoading">
              {{ isLoading ? '提交中...' : '重置密码' }}
            </button>
          </div>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch, computed } from 'vue'
import { ACTION_DB } from '../utils/actionDb'

// ================= 用户数据隔离工具 =================
const currentAccount = ref(localStorage.getItem('theking_current_account') || '')

const USER_DATA_KEYS = new Set([
  'theking_profile', 'theking_diet_plan', 'theking_workout_logs',
  'theking_selected_workout'
])
const uKey = (key: string): string => {
  if (!currentAccount.value) return key
  if (key.startsWith('theking_w_')) return `${key}_${currentAccount.value}`
  if (USER_DATA_KEYS.has(key)) return `${key}_${currentAccount.value}`
  return key
}

const uGetItem = (key: string): string | null => localStorage.getItem(uKey(key))
const uSetItem = (key: string, value: string): void => localStorage.setItem(uKey(key), value)
const uRemoveItem = (key: string): void => localStorage.removeItem(uKey(key))
// ===============================================

// 基础状态
const activePage = ref('home')
const activeGymTab = ref('chest')
const activeDietTab = ref('training')
const isMenuOpen = ref(false)

// ================= 登录/注册/忘记密码 相关逻辑 =================
const isLoginModalOpen = ref(false)
const isLoading = ref(false)
const isLoggedIn = ref(!!localStorage.getItem('theking_token'))
const authTab = ref<'login' | 'register' | 'forgot'>('login')

const loginForm = reactive({ account: '', password: '' })
const registerForm = reactive({ account: '', password: '', confirmPassword: '', email: '', captchaId: '', captchaCode: '' })
const captchaImage = ref('')
const forgotForm = reactive({ account: '', email: '', code: '', newPassword: '', confirmPassword: '' })
const codeCountdown = ref(0)

// AI 体重预测相关
const weightHistory = ref<number[]>([])
const weightPrediction = reactive({
  loading: false,
  error: '',
  data: null as any
})

// 热量计算相关
const calorieForm = reactive({
  weight: '',
  height: '',
  age: '',
  gender: 'male',
  activity: 'moderate'
})

const calorieResult = reactive({
  loading: false,
  data: null as any
})

const calorieHistory = ref<any[]>([])

const calculateCalories = async () => {
  if (!calorieForm.weight || !calorieForm.height || !calorieForm.age) {
    alert('请填写完整信息')
    return
  }
  
  calorieResult.loading = true
  try {
    const token = localStorage.getItem('theking_token')
    const headers: Record<string, string> = { 'Content-Type': 'application/json' }
    if (token) headers['Authorization'] = 'Bearer ' + token
    
    const response = await fetch(`${API_BASE}/api/ai/predict-calories`, {
      method: 'POST',
      headers,
      body: JSON.stringify({
        weight: Number(calorieForm.weight),
        height: Number(calorieForm.height),
        age: Number(calorieForm.age),
        gender: calorieForm.gender,
        activity: calorieForm.activity
      })
    })
    
    const result = await response.json()
    if (result.code === 200) {
      calorieResult.data = result.data
      // 刷新历史记录
      loadCalorieHistory()
    } else {
      alert(result.message || '计算失败')
    }
  } catch (e) {
    alert('网络错误')
  } finally {
    calorieResult.loading = false
  }
}

const loadCalorieHistory = async () => {
  try {
    const token = localStorage.getItem('theking_token')
    if (!token) return
    
    const response = await fetch(`${API_BASE}/api/ai/calorie-history`, {
      headers: { 'Authorization': 'Bearer ' + token }
    })
    
    const result = await response.json()
    if (result.code === 200) {
      calorieHistory.value = result.data
    }
  } catch (e) {
    console.error('加载热量历史失败:', e)
  }
}


const loadWeightPrediction = async () => {
  // 从本地存储加载历史体重数据（模拟数据，实际应从后端获取）
  const stored = localStorage.getItem('theking_weight_history')
  if (stored) {
    weightHistory.value = JSON.parse(stored)
  } else {
    // 生成模拟数据用于演示
    weightHistory.value = generateMockWeightData()
    localStorage.setItem('theking_weight_history', JSON.stringify(weightHistory.value))
  }
  
  if (weightHistory.value.length < 7) {
    weightPrediction.error = '需要至少7天的体重记录'
    return
  }
  
  weightPrediction.loading = true
  weightPrediction.error = ''
  
  try {
    const token = localStorage.getItem('theking_token')
    const headers: Record<string, string> = { 'Content-Type': 'application/json' }
    if (token) headers['Authorization'] = 'Bearer ' + token
    
    const response = await fetch(`${API_BASE}/api/ai/predict-weight`, {
      method: 'POST',
      headers,
      body: JSON.stringify({ history: weightHistory.value })
    })
    
    const result = await response.json()
    if (result.code === 200) {
      weightPrediction.data = result.data
    } else {
      weightPrediction.error = result.message || '预测失败'
    }
  } catch (e) {
    weightPrediction.error = '网络错误，请稍后重试'
    console.error('体重预测失败:', e)
  } finally {
    weightPrediction.loading = false
  }
}

const generateMockWeightData = () => {
  // 生成30天模拟体重数据（缓慢下降趋势）
  const data: number[] = []
  let weight = 75.0
  for (let i = 0; i < 30; i++) {
    weight += (Math.random() - 0.6) * 0.5  // 总体缓慢下降
    data.push(Math.round(weight * 10) / 10)
  }
  return data
}

const API_BASE = 'http://120.24.236.105'

const openLoginModal = () => {
  isLoginModalOpen.value = true
  authTab.value = 'login'
  document.body.style.overflow = 'hidden'
  refreshCaptcha()
}

const closeLoginModal = () => {
  isLoginModalOpen.value = false
  document.body.style.overflow = ''
}

const handleLogin = async () => {
  if (!loginForm.account || !loginForm.password) {
    alert('请输入账号和密码')
    return
  }
  isLoading.value = true
  try {
    const response = await fetch(`${API_BASE}/api/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account: loginForm.account, password: loginForm.password })
    })
    const data = await response.json()
    if (response.ok && data.code === 200) {
      alert('登录成功')
      localStorage.setItem('theking_token', data.token)
      localStorage.setItem('theking_current_account', loginForm.account)
      currentAccount.value = loginForm.account
      isLoggedIn.value = true
      reloadUserData()
      closeLoginModal()
      // 埋点：记录用户登录活动
      trackActivity('LOGIN')
    } else {
      alert(data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录请求错误:', error)
    alert('网络错误，请检查后端服务')
  } finally {
    isLoading.value = false
  }
}

// 埋点辅助函数：记录用户活动
const trackActivity = async (activityType: string) => {
  try {
    const token = localStorage.getItem('theking_token')
    const headers: Record<string, string> = { 'Content-Type': 'application/json' }
    if (token) headers['Authorization'] = 'Bearer ' + token
    await fetch(`${API_BASE}/api/activity`, {
      method: 'POST',
      headers,
      body: JSON.stringify({ activityType })
    })
  } catch (e) {
    // 埋点失败静默处理，不影响主流程
    console.log('Activity tracking failed:', e)
  }
}

const refreshCaptcha = async () => {
  console.log('【调试】refreshCaptcha 被调用')
  try {
    const res = await fetch(`${API_BASE}/api/captcha`)
    const data = await res.json()
    console.log('【调试】验证码接口返回:', data)
    if (data.code === 200) {
      captchaImage.value = data.data.image
      registerForm.captchaId = data.data.captchaId
      console.log('【调试】验证码已设置, captchaId:', data.data.captchaId)
    } else {
      console.error('【调试】验证码接口返回错误:', data)
    }
  } catch (e) {
    console.error('【调试】获取验证码失败', e)
    alert('获取验证码失败，请检查网络连接')
  }
}

const handleRegister = async () => {
  const { account, password, confirmPassword, email, captchaId, captchaCode } = registerForm
  if (!account || !password || !confirmPassword || !email || !captchaCode) return alert('请填写完整信息')
  if (password !== confirmPassword) return alert('两次输入的密码不一致')
  if (password.length < 6) return alert('密码长度至少6位')

  isLoading.value = true
  try {
    const response = await fetch(`${API_BASE}/api/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account, password, email, captchaId, captchaCode })
    })
    const data = await response.json()
    if (data.code === 200) {
      alert('注册成功！请登录')
      authTab.value = 'login'
      loginForm.account = account
      loginForm.password = ''
      registerForm.account = ''
      registerForm.password = ''
      registerForm.confirmPassword = ''
      registerForm.email = ''
      registerForm.captchaCode = ''
    } else {
      alert(data.message || '注册失败')
      refreshCaptcha()
    }
  } catch (error) {
    console.error('注册请求错误:', error)
    alert('网络错误')
  } finally {
    isLoading.value = false
  }
}

const sendForgotCode = async () => {
  if (!forgotForm.account || !forgotForm.email) return alert('请先输入账号和邮箱')
  try {
    const response = await fetch(`${API_BASE}/api/send-code`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account: forgotForm.account, email: forgotForm.email, type: 'forgot' })
    })
    const data = await response.json()
    if (data.code === 200) {
      alert('验证码已发送（请查看后端控制台日志获取验证码）')
      if (data.debugCode) console.log('【开发模式】验证码:', data.debugCode)
      codeCountdown.value = 60
      const timer = setInterval(() => {
        codeCountdown.value--
        if (codeCountdown.value <= 0) clearInterval(timer)
      }, 1000)
    } else {
      alert(data.message || '发送失败')
    }
  } catch (error) {
    console.error('发送验证码错误:', error)
    alert('网络错误')
  }
}

const handleForgot = async () => {
  const { account, email, code, newPassword, confirmPassword } = forgotForm
  if (!account || !email || !code || !newPassword || !confirmPassword) return alert('请填写完整信息')
  if (newPassword !== confirmPassword) return alert('两次输入的新密码不一致')
  if (newPassword.length < 6) return alert('密码长度至少6位')

  isLoading.value = true
  try {
    const response = await fetch(`${API_BASE}/api/reset-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account, email, code, newPassword })
    })
    const data = await response.json()
    if (data.code === 200) {
      alert('密码重置成功！请用新密码登录')
      authTab.value = 'login'
      loginForm.account = account
      loginForm.password = ''
      forgotForm.account = ''
      forgotForm.email = ''
      forgotForm.code = ''
      forgotForm.newPassword = ''
      forgotForm.confirmPassword = ''
    } else {
      alert(data.message || '重置失败')
    }
  } catch (error) {
    console.error('重置密码错误:', error)
    alert('网络错误')
  } finally {
    isLoading.value = false
  }
}
// ===============================================

const reloadUserData = () => {
  const savedProfile = uGetItem('theking_profile')
  if (savedProfile) {
    Object.assign(userProfile, JSON.parse(savedProfile))
  } else {
    Object.assign(userProfile, defaultProfile)
  }

  const savedDiet = uGetItem('theking_diet_plan')
  if (savedDiet) {
    Object.assign(userDietPlan, JSON.parse(savedDiet))
  } else {
    Object.assign(userDietPlan, JSON.parse(JSON.stringify(defaultDietPlan)))
  }

  loadWorkoutLogs()

  const savedSelected = uGetItem('theking_selected_workout')
  selectedWorkoutId.value = savedSelected ? Number(savedSelected) : null

  Object.entries(gymData).forEach(([secId, blocks]) => {
    if (secId === 'back') {
      const backBlocks = blocks as any
      backBlocks.normal.forEach((ex: any) => {
        const k = getWeightKey(secId, ex.name)
        exerciseWeights[k] = uGetItem(k) || ex.default || ''
      })
      backBlocks.variant.forEach((ex: any) => {
        const k = getWeightKey(secId, ex.name)
        exerciseWeights[k] = uGetItem(k) || ex.default || ''
      })
    } else {
      const standardBlocks = blocks as any[]
      standardBlocks.forEach(b => {
        b.list.forEach((ex: any) => {
          const k = getWeightKey(secId, ex.name)
          exerciseWeights[k] = uGetItem(k) || ex.default || ''
        })
      })
    }
  })
}

// ================= 设置与个人资料 =================
const settingsConfig = reactive({
  notifications: localStorage.getItem('theking_notif') === 'true',
  darkMode: localStorage.getItem('theking_dark') === 'true'
})

watch(() => settingsConfig.notifications, (val) => localStorage.setItem('theking_notif', String(val)))

const defaultProfile = { age: '20', height: '', weight: '', gender: 'male' }
const storedProfile = uGetItem('theking_profile')
const userProfile = reactive(storedProfile ? JSON.parse(storedProfile) : defaultProfile)

const saveProfile = () => uSetItem('theking_profile', JSON.stringify(userProfile))

const toggleDarkMode = () => {
  localStorage.setItem('theking_dark', String(settingsConfig.darkMode))
  if (settingsConfig.darkMode) {
    document.body.classList.add('dark-theme')
  } else {
    document.body.classList.remove('dark-theme')
  }
}

const clearCache = () => {
  if (confirm('确定要清除当前账号的所有本地训练记录和数据吗？这将无法恢复。')) {
    const keys = Object.keys(localStorage)
    keys.forEach(k => {
      if (k.startsWith('theking_') && !k.startsWith('theking_token') && !k.startsWith('theking_dark') && !k.startsWith('theking_notif') && !k.startsWith('theking_current_account')) {
        if (currentAccount.value && k.endsWith(`_${currentAccount.value}`)) localStorage.removeItem(k)
      }
    })
    Object.assign(userProfile, defaultProfile)
    Object.assign(userDietPlan, JSON.parse(JSON.stringify(defaultDietPlan)))
    workoutLogs.value = []
    selectedWorkoutId.value = null
    Object.keys(exerciseWeights).forEach(k => { exerciseWeights[k] = '' })
    alert('当前账号缓存已清除！')
    location.reload()
  }
}

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('theking_token')
    localStorage.removeItem('theking_user')
    localStorage.removeItem('theking_current_account')
    currentAccount.value = ''
    isLoggedIn.value = false
    Object.assign(userProfile, defaultProfile)
    Object.assign(userDietPlan, JSON.parse(JSON.stringify(defaultDietPlan)))
    workoutLogs.value = []
    selectedWorkoutId.value = null
    Object.keys(exerciseWeights).forEach(k => { exerciseWeights[k] = '' })
    alert('已退出登录')
  }
}

onMounted(() => {
  if (settingsConfig.darkMode) document.body.classList.add('dark-theme')
  if (currentAccount.value) reloadUserData()
  // 加载热量计算历史
  loadCalorieHistory()
  // 加载热量计算历史
  loadCalorieHistory()
  // 埋点：记录用户浏览健身内容
  trackActivity('VIEW_EXERCISE')
})
// ===============================================

// ================= 全新饮食与排期逻辑 =================
const defaultDietPlan = {
  '周一': { isRest: false, part: '腿部', brunch: '鸡腿×2 + 米饭200g + 青菜', pre: '碱水包 + 肌酸', post: '蛋白粉1.5勺 + 香蕉 + 碱水包', dinner: '鸡腿 + 鸭胗 + 蔬菜 + 碱水包', cal: 1710, protein: '134g' },
  '周二': { isRest: false, part: '肩臂', brunch: '鸡腿×2 + 米饭200g + 番茄炒蛋', pre: '碱水包 + 肌酸', post: '蛋白粉1.5勺 + 香蕉 + 碱水包', dinner: '鸡腿 + 鸡蛋 + 蔬菜 + 碱水包', cal: 1750, protein: '138g' },
  '周三': { isRest: false, part: '胸背', brunch: '鸡腿×2 + 米饭200g + 青菜', pre: '碱水包 + 肌酸', post: '蛋白粉1.5勺 + 香蕉 + 碱水包', dinner: '鸭胗×2 + 蔬菜 + 米饭', cal: 1680, protein: '130g' },
  '周四': { isRest: false, part: '腿部', brunch: '鸡腿×2 + 米饭200g + 青菜', pre: '碱水包 + 肌酸', post: '蛋白粉1.5勺 + 香蕉 + 碱水包', dinner: '鸡腿 + 鸭胗 + 蔬菜 + 碱水包', cal: 1710, protein: '134g' },
  '周五': { isRest: true, part: '休息日', brunch: '鸡腿×1 + 鸭胗×1 + 米饭150g + 青菜', pre: '—', post: '—', dinner: '鸡腿 + 蔬菜 + 碱水包', cal: 1450, protein: '108g' },
  '周六': { isRest: true, part: '休息日', brunch: '鸡腿×1 + 米饭150g + 青菜', pre: '—', post: '—', dinner: '鸡腿 + 蔬菜 + 碱水包', cal: 1380, protein: '105g' },
  '周日': { isRest: false, part: '有氧', brunch: '鸡腿×1 + 米饭150g + 青菜', pre: '—', post: '蛋白粉1勺', dinner: '鸡腿 + 蔬菜 + 碱水包', cal: 1520, protein: '115g' }
}

const savedDietJSON = uGetItem('theking_diet_plan')
const userDietPlan = reactive<Record<string, any>>(savedDietJSON ? JSON.parse(savedDietJSON) : JSON.parse(JSON.stringify(defaultDietPlan)))

const saveDietSchedule = () => {
  uSetItem('theking_diet_plan', JSON.stringify(userDietPlan))
  isEditScheduleModalOpen.value = false
}

const trainingDays = computed(() => Object.entries(userDietPlan).filter(([day, data]) => !data.isRest).map(([day, data]) => ({ day, ...data })))
const restDays = computed(() => Object.entries(userDietPlan).filter(([day, data]) => data.isRest).map(([day, data]) => ({ day, ...data })))

const getBadgeClass = (part: string) => {
  if (!part) return 'badge-rest'
  if (part.includes('腿')) return 'badge-leg'
  if (part.includes('胸')) return 'badge-chest'
  if (part.includes('肩')) return 'badge-shoulder'
  if (part.includes('氧')) return 'badge-aerobic'
  return 'badge-rest'
}

const isEditScheduleModalOpen = ref(false)
const isDietEntryModalOpen = ref(false)
const dietEntryTab = ref<'manual' | 'file'>('manual')
const smartParseText = ref('')
const dietFileInput = ref<HTMLInputElement | null>(null)

const manualDietForm = reactive({
  day: '周一',
  brunch: '',
  pre: '',
  post: '',
  dinner: '',
  cal: '',
  protein: ''
})

const openDietEntryModal = () => {
  const currentData = userDietPlan[manualDietForm.day]
  if(currentData) {
    manualDietForm.brunch = currentData.brunch || ''
    manualDietForm.pre = currentData.pre && currentData.pre !== '—' ? currentData.pre : ''
    manualDietForm.post = currentData.post && currentData.post !== '—' ? currentData.post : ''
    manualDietForm.dinner = currentData.dinner || ''
    manualDietForm.cal = currentData.cal || ''
    manualDietForm.protein = currentData.protein || ''
  }
  isDietEntryModalOpen.value = true
  document.body.style.overflow = 'hidden'
}

watch(() => manualDietForm.day, (newDay) => {
  const currentData = userDietPlan[newDay]
  if(currentData) {
    manualDietForm.brunch = currentData.brunch || ''
    manualDietForm.pre = currentData.pre && currentData.pre !== '—' ? currentData.pre : ''
    manualDietForm.post = currentData.post && currentData.post !== '—' ? currentData.post : ''
    manualDietForm.dinner = currentData.dinner || ''
    manualDietForm.cal = currentData.cal || ''
    manualDietForm.protein = currentData.protein || ''
  } else {
    manualDietForm.brunch = ''
    manualDietForm.pre = ''
    manualDietForm.post = ''
    manualDietForm.dinner = ''
    manualDietForm.cal = ''
    manualDietForm.protein = ''
  }
})

const saveManualDiet = () => {
  if (!userDietPlan[manualDietForm.day]) {
    userDietPlan[manualDietForm.day] = { isRest: false, part: '训练日' }
  }
  userDietPlan[manualDietForm.day].brunch = manualDietForm.brunch
  userDietPlan[manualDietForm.day].pre = manualDietForm.pre || '—'
  userDietPlan[manualDietForm.day].post = manualDietForm.post || '—'
  userDietPlan[manualDietForm.day].dinner = manualDietForm.dinner
  userDietPlan[manualDietForm.day].cal = manualDietForm.cal
  userDietPlan[manualDietForm.day].protein = manualDietForm.protein
  
  saveDietSchedule()
  alert(`${manualDietForm.day} 饮食记录已保存`)
}

const triggerDietFile = () => dietFileInput.value?.click()

const handleSmartImport = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = (ev) => {
    smartParseText.value = ev.target?.result as string
    parseTextToDiet()
  }
  reader.readAsText(file)
  if (dietFileInput.value) dietFileInput.value.value = ''
}

const parseTextToDiet = () => {
  const text = smartParseText.value
  if (!text.trim()) return alert('请输入或导入内容')
  
  try {
    const importedData = JSON.parse(text)
    if (importedData && importedData['周一']) {
      Object.assign(userDietPlan, importedData)
      saveDietSchedule()
      alert('JSON格式识别成功！已更新饮食计划。')
      isDietEntryModalOpen.value = false
      document.body.style.overflow = ''
      return
    }
  } catch (e) {
    // ignore json error, proceed to text parse
  }

  let parsedCount = 0;
  const dayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  
  dayNames.forEach(day => {
    const dayIndex = text.indexOf(day);
    if (dayIndex !== -1) {
      let nextDayIndex = text.length;
      dayNames.forEach(d => {
        if (d !== day) {
          const idx = text.indexOf(d, dayIndex + 1);
          if (idx !== -1 && idx < nextDayIndex) {
            nextDayIndex = idx;
          }
        }
      });
      
      const block = text.substring(dayIndex, nextDayIndex);
      const brunchMatch = block.match(/(?:早午餐|早餐|中餐|午餐|早)[:：\s]+([^\n]+)/);
      const dinnerMatch = block.match(/(?:晚餐|晚)[:：\s]+([^\n]+)/);
      const calMatch = block.match(/(?:热量|总热量|大卡|kcal)[:：\s]*(\d+)/i);
      const preMatch = block.match(/(?:练前)[:：\s]+([^\n]+)/);
      const postMatch = block.match(/(?:练后)[:：\s]+([^\n]+)/);
      const proteinMatch = block.match(/(?:蛋白|蛋白质)[:：\s]*([\d.]+[a-zA-Z]*)/);
      
      if (brunchMatch || dinnerMatch || calMatch) {
        if (!userDietPlan[day]) userDietPlan[day] = { isRest: false, part: '提取' };
        const dayPlan = userDietPlan[day]!;
        if (brunchMatch) dayPlan.brunch = brunchMatch[1]!.trim();
        if (dinnerMatch) dayPlan.dinner = dinnerMatch[1]!.trim();
        if (calMatch) dayPlan.cal = calMatch[1]!.trim();
        if (preMatch) dayPlan.pre = preMatch[1]!.trim();
        if (postMatch) dayPlan.post = postMatch[1]!.trim();
        if (proteinMatch) dayPlan.protein = proteinMatch[1]!.trim();
        parsedCount++;
      }
    }
  });

  if (parsedCount > 0) {
    saveDietSchedule()
    alert(`智能解析成功！识别到 ${parsedCount} 天的饮食记录并已更新。`)
    isDietEntryModalOpen.value = false
    document.body.style.overflow = ''
  } else {
    alert('未识别到有效格式。请确保包含“周一”、“早餐/早午餐”、“晚餐”、“热量”等关键字。')
  }
}

const dayNamesArr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
const todayName = computed(() => dayNamesArr[new Date().getDay()])

const todayDietIntake = computed(() => {
  const todayData = userDietPlan[todayName.value as string]
  return todayData && todayData.cal ? Number(todayData.cal) : 0
})

const todayDeficit = computed(() => {
  const tdee = calculatedStats.value.tdee
  if (!tdee || tdee === 0) return 0
  return tdee - todayDietIntake.value
})
// ===============================================

// ================= 训练记录模板逻辑 (多选部位版) =================
const workoutLogs = ref<any[]>([])
const isAddWorkoutOpen = ref(false)
const selectedWorkoutId = ref<number | null>(Number(uGetItem('theking_selected_workout')) || null)

const loadWorkoutLogs = () => {
  const saved = uGetItem('theking_workout_logs')
  if (saved) {
    workoutLogs.value = JSON.parse(saved)
  } else {
    workoutLogs.value = []
  }
}

const saveLogsToStorage = () => uSetItem('theking_workout_logs', JSON.stringify(workoutLogs.value))

const selectWorkout = (id: number) => {
  if (selectedWorkoutId.value === id) {
    selectedWorkoutId.value = null
    uRemoveItem('theking_selected_workout')
  } else {
    selectedWorkoutId.value = id
    uSetItem('theking_selected_workout', String(id))
  }
}

// 目标部位改为数组形式，支持多选
const newWorkoutForm = reactive({
  parts: ['chest']
})

const availableExercises = ref<any[]>([])

const blurInput = (e: Event) => {
  const target = e.target as HTMLInputElement | null
  target?.blur()
}

const updateAvailableExercises = () => {
  let rawList: string[] = []
  
  newWorkoutForm.parts.forEach(part => {
    const partData = gymData[part as keyof typeof gymData]
    if (!partData) return
    
    if (Array.isArray(partData)) {
      partData.forEach((block: any) => block.list.forEach((ex: any) => rawList.push(ex.name)))
    } else if (typeof partData === 'object') {
      if ((partData as any).normal) (partData as any).normal.forEach((ex: any) => rawList.push(ex.name))
      if ((partData as any).variant) (partData as any).variant.forEach((ex: any) => rawList.push(ex.name))
    }
  })
  
  const uniqueNames = [...new Set(rawList.map(n => n.replace(/\s*x\d+组.*/, '').trim()))]
  availableExercises.value = uniqueNames.map(name => ({
    name,
    selected: false,
    weight: '',
    sets: '4'
  }))
}

const openAddWorkoutModal = () => {
  newWorkoutForm.parts = ['chest']
  updateAvailableExercises()
  isAddWorkoutOpen.value = true
  document.body.style.overflow = 'hidden'
}

const closeAddWorkoutModal = () => {
  isAddWorkoutOpen.value = false
  document.body.style.overflow = ''
}

const partNamesMap: Record<string, string> = {
  chest: '胸部', back: '背部', shoulder: '肩部', legs: '腿部', arms: '手臂'
}

const saveWorkoutLog = () => {
  if (newWorkoutForm.parts.length === 0) {
    alert('请至少选择一个目标部位')
    return
  }

  const selectedExs = availableExercises.value.filter(ex => ex.selected && ex.sets)
  if (selectedExs.length === 0) {
    alert('请至少勾选并填写一个动作')
    return
  }
  
  let totalSets = 0
  const finalExercises = selectedExs.map(ex => {
    const s = parseInt(ex.sets) || 0
    totalSets += s
    return { name: ex.name, weight: ex.weight || '自重/常规', sets: s }
  })
  
  const partNameStr = newWorkoutForm.parts.map(p => partNamesMap[p]).join(' + ')

  const newLog = {
    id: Date.now(),
    part: newWorkoutForm.parts,
    partName: partNameStr,
    exercises: finalExercises,
    totalSets
  }
  
  workoutLogs.value.unshift(newLog)
  saveLogsToStorage()
  closeAddWorkoutModal()
}

const deleteLog = (id: number) => {
  if (confirm('确定删除该训练模板吗？')) {
    if (selectedWorkoutId.value === id) {
      selectedWorkoutId.value = null
      uRemoveItem('theking_selected_workout')
    }
    workoutLogs.value = workoutLogs.value.filter(log => log.id !== id)
    saveLogsToStorage()
  }
}
// ===============================================

// ================= 数据统计 (BMI/BMR/TDEE) =================
const calculatedStats = computed(() => {
  const h = Number(userProfile.height)
  const w = Number(userProfile.weight)
  const a = Number(userProfile.age) || 20

  if (!h || !w) return { bmi: '0.0', bmr: 0, tdee: 0, bmiStatus: '未设置', bmiClass: '' }

  const bmi = (w / ((h / 100) * (h / 100))).toFixed(1)
  let bmiStatus = '正常'
  let bmiClass = 'text-green'
  if (Number(bmi) < 18.5) {
    bmiStatus = '偏瘦'
    bmiClass = 'text-blue'
  } else if (Number(bmi) >= 24 && Number(bmi) < 28) {
    bmiStatus = '偏胖'
    bmiClass = 'text-orange'
  } else if (Number(bmi) >= 28) {
    bmiStatus = '肥胖'
    bmiClass = 'text-red'
  }

  let bmr = 0
  if (userProfile.gender === 'male') {
    bmr = 10 * w + 6.25 * h - 5 * a + 5
  } else {
    bmr = 10 * w + 6.25 * h - 5 * a - 161
  }

  let workoutCalories = 0
  if (selectedWorkoutId.value) {
    const selectedTemplate = workoutLogs.value.find(log => log.id === selectedWorkoutId.value)
    if (selectedTemplate) {
      workoutCalories = selectedTemplate.totalSets * 15
    }
  }

  const tdee = Math.round((bmr * 1.2) + workoutCalories)

  return { bmi, bmr: Math.round(bmr), tdee, bmiStatus, bmiClass }
})
// ===============================================

const completedExercises = reactive<Record<string, boolean>>({})

let touchStartX = 0
const handleTouchStart = (e: TouchEvent) => {
  if (e.touches.length > 0) touchStartX = e.touches[0]!.clientX
}
const handleTouchEnd = (e: TouchEvent) => {
  if (e.changedTouches.length === 0) return
  const touchEndX = e.changedTouches[0]!.clientX
  const diff = touchEndX - touchStartX
  if (touchStartX < 30 && diff > 50 && !isMenuOpen.value) {
    isMenuOpen.value = true
  }
}

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const showPage = (pageName: string) => {
  activePage.value = pageName
  isMenuOpen.value = false
  window.scrollTo(0, 0)
  
  // 切换到数据面板时自动加载 AI 预测
  if (pageName === 'stats') {
    loadWeightPrediction()
  }
}

const toggleCompleted = (key: string, event: Event) => {
  if ((event.target as HTMLElement).tagName === 'INPUT') return
  completedExercises[key] = !completedExercises[key]
}

const isModalOpen = ref(false)
const modalActionName = ref('')
const modalData = ref<any>(null)

const openActionModal = (actionName: string) => {
  modalActionName.value = actionName
  let data = ACTION_DB[actionName]
  if (!data) {
    const clean = actionName.replace(/\s*x\d+组.*/, '').replace(/\s*\(\S+\)/, '').trim()
    data = ACTION_DB[clean]
    if (!data) {
      for (const key in ACTION_DB) {
        if (clean.includes(key) || key.includes(clean)) {
          data = ACTION_DB[key]
          break
        }
      }
    }
  }
  modalData.value = data
  isModalOpen.value = true
  document.body.style.overflow = 'hidden'
}

const closeActionModal = () => {
  isModalOpen.value = false
  document.body.style.overflow = ''
}

const handleEsc = (e: KeyboardEvent) => {
  if (e.key === 'Escape') {
    closeActionModal()
    closeLoginModal()
    closeAddWorkoutModal()
    isEditScheduleModalOpen.value = false
    isDietEntryModalOpen.value = false
  }
}
onMounted(() => window.addEventListener('keydown', handleEsc))
onUnmounted(() => window.removeEventListener('keydown', handleEsc))

const getWeightKey = (sectionId: string, actionName: any): string => {
  const safeName = String(actionName).replace(/[^\w\u4e00-\u9fa5]/g, '_').substring(0, 40)
  return `theking_w_${sectionId}_${safeName}`
}

const exerciseWeights = reactive<Record<string, string>>({})

const saveWeight = (key: any) => {
  const sKey = String(key)
  uSetItem(sKey, exerciseWeights[sKey] || '')
}
const trimWeight = (key: any) => {
  const sKey = String(key)
  if (exerciseWeights[sKey]) {
    exerciseWeights[sKey] = exerciseWeights[sKey].trim()
    uSetItem(sKey, exerciseWeights[sKey] || '')
  }
}

const gymData: any = {
  chest: [
    { title: '常规项目', list: [
      { name: '杠铃推胸 x4组 / 哑铃推胸 x4组', placeholder: '60kg', default: '60kg' },
      { name: '杠铃推上斜 x4组 / 哑铃推上斜 x4组', placeholder: '重量', default: '' },
      { name: '蝴蝶机夹胸 x4组', placeholder: '重量', default: '' },
      { name: '器械推胸 x4组', placeholder: '重量', default: '' },
      { name: '龙门架夹胸 x4组', placeholder: '重量', default: '' },
      { name: '三头绳索下压 x4组', placeholder: '重量', default: '' }
    ]},
    { title: '变式项目 (补充)', list: [
      { name: '杠铃推胸 x4组 / 哑铃推胸 x4组', placeholder: '重量', default: '' },
      { name: '杠铃推上斜 x4组 / 哑铃推上斜 x4组', placeholder: '重量', default: '' },
      { name: '蝴蝶机夹胸 x4组', placeholder: '重量', default: '' },
      { name: '器械推胸 x4组', placeholder: '重量', default: '' },
      { name: '飞鸟递减组(三组重) x4组', placeholder: '重量', default: '' },
      { name: '直臂蝴蝶机反向飞鸟 x4组', placeholder: '重量', default: '' },
      { name: '悬垂举腿 x4组 (可选)', placeholder: '重量', default: '' }
    ]},
    { title: '对抗肌训练', list: [
      { name: '杠铃推胸 x4组 / 哑铃推胸 x4组', placeholder: '重量', default: '' },
      { name: '杠铃推上斜 x4组 / 哑铃推上斜 x4组', placeholder: '重量', default: '' },
      { name: '蝴蝶机夹胸 x4组', placeholder: '重量', default: '' },
      { name: '器械推胸 x4组', placeholder: '重量', default: '' },
      { name: '高位下拉 x4组', placeholder: '重量', default: '' },
      { name: '哑铃划船 x4组', placeholder: '重量', default: '' },
      { name: '悬垂举腿 x4组 (可选)', placeholder: '重量', default: '' }
    ]}
  ],
  back: {
    normal: [
      { name: '引体向上 x4组', placeholder: '重量' },
      { name: '高位下拉 x4组', placeholder: '50kg', default: '50kg' },
      { name: '坐姿器械划船 x4组', placeholder: '重量' },
      { name: '直臂下压 x4组', placeholder: '重量' },
      { name: '坐姿绳索划船 x4组', placeholder: '重量' },
      { name: '二头弯举 x4组', placeholder: '重量' }
    ],
    variant: [
      { name: '引体向上 x4组', placeholder: '重量' },
      { name: '高位下拉 x4组', placeholder: '重量' },
      { name: '坐姿器械划船 x4组', placeholder: '重量' },
      { name: '直臂下压 x4组', placeholder: '重量' },
      { name: '坐姿绳索划船 x4组', placeholder: '重量' },
      { name: '二头弯举 x4组', placeholder: '重量' },
      { name: '蝴蝶机反向飞鸟 x4组', placeholder: '重量' },
      { name: '面拉 x4组', placeholder: '重量' }
    ]
  },
  shoulder: [
    { title: '常规项目', list: [
      { name: '哑铃推肩 x4组 / 杠铃推肩 x4组', placeholder: '27.5kg', default: '27.5kg' },
      { name: '飞鸟递减组(三组重) x4组', placeholder: '重量', default: '' },
      { name: '蝴蝶机反向飞鸟 x4组', placeholder: '重量', default: '' },
      { name: '侧平举 x4组', placeholder: '重量', default: '' },
      { name: '面拉 x4组', placeholder: '重量', default: '' },
      { name: '悬垂举腿 x4组', placeholder: '重量', default: '' },
      { name: '二头弯举 x4组', placeholder: '重量', default: '' }
    ]},
    { title: '变式项目', list: [
      { name: '哑铃推肩 x4组 / 杠铃推肩 x4组', placeholder: '重量', default: '' },
      { name: '飞鸟递减组(三组重) x4组', placeholder: '重量', default: '' },
      { name: '蝴蝶机反向飞鸟 x4组', placeholder: '重量', default: '' },
      { name: '侧平举 x4组', placeholder: '重量', default: '' },
      { name: '面拉 x4组', placeholder: '重量', default: '' },
      { name: '三头肌绳索下压 x4组', placeholder: '重量', default: '' },
      { name: '二头弯举 x4组', placeholder: '重量', default: '' }
    ]},
    { title: '变式项目 2', list: [
      { name: '哑铃推肩 x4组 / 杠铃推肩 x4组', placeholder: '重量', default: '' },
      { name: '飞鸟递减组(三组重) x4组', placeholder: '重量', default: '' },
      { name: '蝴蝶机反向飞鸟 x4组', placeholder: '重量', default: '' },
      { name: '侧平举 x4组', placeholder: '重量', default: '' },
      { name: '面拉 x4组', placeholder: '重量', default: '' },
      { name: '深蹲 x4组', placeholder: '重量', default: '' }
    ]}
  ],
  legs: [
    { title: '腿部常规', list: [
      { name: '杠铃深蹲 x4组', placeholder: '100kg', default: '100kg' },
      { name: '倒蹬/腿举 x4组', placeholder: '重量', default: '' },
      { name: '腿弯举 x4组', placeholder: '重量', default: '' },
      { name: '腿屈伸 x4组', placeholder: '重量', default: '' },
      { name: '罗马尼亚硬拉 x4组', placeholder: '重量', default: '' },
      { name: '提踵 x4组', placeholder: '重量', default: '' }
    ]},
    { title: '有氧 & 核心', list: [
      { name: '坡度走 20分钟（坡度20，速度4.0）', placeholder: '重量', default: '' },
      { name: '平板支撑 x3组', placeholder: '重量', default: '' },
      { name: '悬垂举腿 x4组', placeholder: '重量', default: '' }
    ]}
  ],
  arms: [
    { title: '肱二头肌', list: [
      { name: '杠铃弯举 x4组', placeholder: '重量', default: '' },
      { name: '哑铃锤式弯举 x4组', placeholder: '重量', default: '' },
      { name: '牧师凳弯举 x4组', placeholder: '重量', default: '' }
    ]},
    { title: '肱三头肌', list: [
      { name: '绳索下压 x4组', placeholder: '重量', default: '' },
      { name: '仰卧臂屈伸 x4组', placeholder: '重量', default: '' },
      { name: '窄距卧推 x4组', placeholder: '重量', default: '' },
      { name: '过顶臂屈伸 x4组', placeholder: '重量', default: '' }
    ]}
  ]
}

Object.entries(gymData).forEach(([secId, blocks]) => {
  if (secId === 'back') {
    const backBlocks = blocks as any
    backBlocks.normal.forEach((ex: any) => {
      const k = getWeightKey(secId, ex.name)
      exerciseWeights[k] = uGetItem(k) || ex.default || ''
    })
    backBlocks.variant.forEach((ex: any) => {
      const k = getWeightKey(secId, ex.name)
      exerciseWeights[k] = uGetItem(k) || ex.default || ''
    })
  } else {
    const standardBlocks = blocks as any[]
    standardBlocks.forEach(b => {
      b.list.forEach((ex: any) => {
        const k = getWeightKey(secId, ex.name)
        exerciseWeights[k] = uGetItem(k) || ex.default || ''
      })
    })
  }
})
</script>

<style scoped>
/* ========== 基础重置 & 变量 ========== */
.home-container {
  --primary: #007ACC;
  --primary-dark: #005fa3;
  --accent: #ff4500;
  --bg-dark: #121212;
  --card-bg: #1e1e1e;
  --text-main: #333;
  --text-light: #e0e0e0;
  --text-sub: #a0a0a0;
  --shadow-soft: 0 10px 30px -5px rgba(0,0,0,0.08);
  --transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: #f5f5f7;
  color: var(--text-main);
  min-height: 100vh;
  position: relative;
  transition: background 0.3s ease, color 0.3s ease;
}

* { box-sizing: border-box; -webkit-tap-highlight-color: transparent; }

/* 深色模式基础 */
body.dark-theme .home-container {
  background: #000;
  --text-main: #f5f5f7;
  --text-sub: #86868b;
  --shadow-soft: 0 10px 30px -5px rgba(255,255,255,0.05);
}
body.dark-theme #home-page #content-card { background: #1c1c1e; }
body.dark-theme #home-page #card-text { border-right-color: #38383a; }
body.dark-theme #home-page #footer-area { background: #1c1c1e; border-color: #38383a; }
body.dark-theme .diet-nav { background: #1c1c1e; }
body.dark-theme .diet-table-wrapper, body.dark-theme .rest-table-wrapper { background: #1c1c1e; }
body.dark-theme .diet-table thead tr { background: #2c2c2e; color: #aaa; }
body.dark-theme .diet-table tbody td { border-bottom-color: #38383a; }
body.dark-theme .diet-table tbody tr:hover { background: #2c2c2e; }
body.dark-theme .tip-card { background: #1c1c1e; }
body.dark-theme .stats-card { background: #1c1c1e; border-color: #38383a; }

/* ========== 汉堡菜单按钮 ========== */
#menu-btn {
  position: fixed; top: 20px; left: 20px; z-index: 1000; width: 44px; height: 44px;
  background: rgba(255,255,255,0.95); border: none; border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.15); cursor: pointer;
  display: flex; flex-direction: column; justify-content: center; align-items: center; gap: 5px;
  transition: var(--transition);
}
body.dark-theme #menu-btn { background: rgba(28,28,30,0.95); box-shadow: 0 2px 10px rgba(0,0,0,0.5); }
#menu-btn span {
  display: block; width: 22px; height: 2.5px; background: var(--text-main);
  border-radius: 2px; transition: var(--transition);
}
#menu-btn.active span:nth-child(1) { transform: rotate(45deg) translate(5px, 5px); }
#menu-btn.active span:nth-child(2) { opacity: 0; }
#menu-btn.active span:nth-child(3) { transform: rotate(-45deg) translate(5px, -5px); }

/* ========== 侧边菜单 ========== */
#side-menu {
  position: fixed; top: 0; left: -280px; width: 280px; height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%); z-index: 999;
  transition: left 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  padding: 80px 0 30px; overflow-y: auto;
}
#side-menu.open { left: 0; }
.menu-item {
  display: flex; align-items: center; padding: 16px 25px; color: var(--text-light);
  font-size: 16px; cursor: pointer; transition: all 0.3s; border-left: 3px solid transparent;
}
.menu-item:hover, .menu-item.active {
  background: rgba(255,255,255,0.05); border-left-color: var(--accent); color: #fff;
}
.menu-item .icon { width: 28px; height: 28px; margin-right: 15px; display: flex; align-items: center; justify-content: center; font-size: 18px; }
.menu-divider { height: 1px; background: rgba(255,255,255,0.1); margin: 15px 20px; }
.menu-header { padding: 0 25px 20px; color: var(--text-sub); font-size: 12px; text-transform: uppercase; letter-spacing: 1px; }

/* ========== 遮罩层 ========== */
#overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100vh;
  background: rgba(0,0,0,0.5); backdrop-filter: blur(3px); z-index: 998;
  opacity: 0; visibility: hidden; transition: all 0.3s;
}
#overlay.show { opacity: 1; visibility: visible; }

/* ========== 页面内容区 ========== */
.page { display: none; min-height: 100vh; }
.page.active { display: block; animation: fadeIn 0.4s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

/* ========== 首页样式 ========== */
#home-page .main-wrapper { width: 100%; max-width: 1200px; margin: 0 auto; padding: 40px 20px; min-height: 100vh; }
#home-page section { padding: 0; margin-bottom: 40px; border-radius: 16px; }
#home-page #header { background: linear-gradient(135deg, #007ACC 0%, #005fa3 100%); color: white; text-align: center; box-shadow: var(--shadow-soft); padding: 40px 20px; border-radius: 16px; margin-top: 60px; }
#home-page #header h1 { font-size: 2.8em; margin-bottom: 12px; font-weight: 700; }
#home-page #header p { font-size: 1.1em; opacity: 0.9; }

#home-page #content-card { display: flex; background: #ffffff; border-radius: 16px; box-shadow: var(--shadow-soft); overflow: hidden; min-height: 420px; transition: var(--transition); }
#home-page #content-card:hover { box-shadow: 0 20px 40px -5px rgba(0,0,0,0.12); transform: translateY(-3px); }
#home-page #card-text { flex: 2; padding: 50px 40px; display: flex; flex-direction: column; justify-content: center; border-right: 1px solid #f0f0f0; }
#home-page #card-text h2 { font-size: 1.8em; margin-bottom: 25px; position: relative; display: inline-block; }
#home-page #card-text h2::after { content: ''; position: absolute; bottom: -8px; left: 0; width: 60px; height: 4px; background: var(--primary); border-radius: 2px; }
#home-page #card-text p { text-align: justify; margin-bottom: 18px; color: var(--text-sub); font-size: 1.05em; line-height: 1.8; }
#home-page #card-text strong { color: var(--primary); font-weight: 600; }
#home-page .bullet-line { height: 1px; background: linear-gradient(to right, #e5e7eb, transparent); margin: 20px 0; }
body.dark-theme #home-page .bullet-line { background: linear-gradient(to right, #38383a, transparent); }
#home-page #card-text a { color: var(--primary); text-decoration: none; font-weight: 600; }
#home-page #card-text a:hover { text-decoration: underline; }

#home-page #card-image { flex: 1; position: relative; overflow: hidden; background: #1a1a1a; min-height: 100%; display: flex; align-items: center; justify-content: center; }
#home-page #card-image img { width: 100%; height: 100%; object-fit: contain; max-width: 100%; max-height: 100%; display: block; transition: transform 0.8s ease; }
#home-page #content-card:hover #card-image img { transform: scale(1.05); }
#home-page #card-image h3 { position: absolute; top: 24px; left: 24px; color: #fff; font-size: 1.4em; font-weight: 700; z-index: 2; background: rgba(255,255,255,0.15); backdrop-filter: blur(12px); padding: 10px 18px; border-radius: 12px; border: 1px solid rgba(255,255,255,0.25); pointer-events: none; }

#home-page #footer-area { background: #fff; text-align: center; border: 2px dashed #e5e7eb; color: #9ca3af; padding: 60px 20px; border-radius: 16px; }

/* ========== 健身页样式 ========== */
#gym-page { background: var(--bg-dark); color: var(--text-light); padding-top: 70px; }
.gym-nav { display: flex; justify-content: space-around; padding: 10px; background: var(--bg-dark); position: sticky; top: 0; z-index: 100; box-shadow: 0 2px 5px rgba(0,0,0,0.5); }
.gym-nav-btn { flex: 1; margin: 0 5px; padding: 12px 0; border: none; border-radius: 8px; font-size: 14px; font-weight: bold; cursor: pointer; transition: all 0.3s ease; background: #333; color: var(--text-sub); }
.gym-nav-btn.active { background: var(--accent); color: #fff; transform: scale(1.05); }

.gym-section { padding: 20px; }
.workout-block { background: var(--card-bg); border-radius: 12px; padding: 15px 20px; margin-bottom: 20px; border: 1px solid #333; }
.block-title { color: var(--accent); font-size: 1.2rem; border-bottom: 1px solid #333; padding-bottom: 10px; margin-top: 0; margin-bottom: 15px; text-transform: uppercase; letter-spacing: 1px; }
.exercise-list { list-style: none; padding: 0; margin: 0; }
.exercise-item { padding: 12px 0; border-bottom: 1px dashed #333; font-size: 1rem; color: var(--text-light); cursor: pointer; transition: color 0.2s; display: flex; justify-content: space-between; align-items: center; }
.exercise-item:last-child { border-bottom: none; }
.exercise-item:hover { color: var(--accent); }
.exercise-item .action-name { flex: 1; cursor: pointer; transition: color 0.2s; user-select: none; }
.exercise-item .action-name:hover { color: #ff6b35; }
.exercise-item .weight-input { background: #333; border: 1px solid #444; color: #fff; padding: 4px 8px; border-radius: 6px; width: 80px; font-size: 14px; text-align: center; }
.exercise-item.completed { text-decoration: line-through; color: #555 !important; opacity: 0.6; }
.note { font-size: 0.85rem; color: #666; margin-top: 10px; font-style: italic; }

/* ========== 动作演示弹窗 ========== */
.action-modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100vh; background: rgba(0,0,0,0.75); backdrop-filter: blur(8px); z-index: 2000; display: flex; align-items: center; justify-content: center; opacity: 0; visibility: hidden; transition: opacity 0.3s ease, visibility 0.3s ease; }
.action-modal-overlay.show { opacity: 1; visibility: visible; }
.action-modal { background: #1e1e2e; border-radius: 20px; width: 90%; max-width: 500px; max-height: 85vh; overflow-y: auto; transform: translateY(30px) scale(0.95); transition: transform 0.35s cubic-bezier(0.34, 1.56, 0.64, 1); box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5); border: 1px solid #333; }
.action-modal-overlay.show .action-modal { transform: translateY(0) scale(1); }
.action-modal-header { padding: 24px 24px 16px; border-bottom: 1px solid #333; position: relative; }
.action-modal-header h3 { font-size: 20px; color: #fff; margin: 0; padding-right: 30px; }
.action-modal-header .target-muscle { font-size: 13px; color: #ff6b35; margin-top: 6px; font-weight: 600; }
.action-modal-close { position: absolute; top: 18px; right: 18px; width: 32px; height: 32px; background: #333; border: none; border-radius: 50%; color: #aaa; font-size: 18px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s; }
.action-modal-close:hover { background: #ff4500; color: #fff; }
.action-modal-body { padding: 20px 24px; }
.action-video-container { width: 100%; aspect-ratio: 16/9; background: #0a0a0a; border-radius: 12px; margin-bottom: 20px; overflow: hidden; display: flex; align-items: center; justify-content: center; position: relative; }
.action-video-container video { width: 100%; height: 100%; object-fit: cover; }
.action-video-placeholder { text-align: center; color: #666; }
.action-video-placeholder .placeholder-icon { font-size: 48px; margin-bottom: 8px; opacity: 0.3; }
.action-steps { margin-bottom: 20px; }
.action-steps h4, .action-tips h4, .action-mistakes h4 { font-size: 14px; color: #ff6b35; margin-bottom: 10px; text-transform: uppercase; letter-spacing: 1px; }
.action-steps ol { padding-left: 18px; margin: 0; }
.action-steps ol li { color: #ccc; font-size: 14px; line-height: 1.7; margin-bottom: 6px; }
.action-tips ul, .action-mistakes ul { list-style: none; padding: 0; margin: 0; }
.action-tips ul li, .action-mistakes ul li { color: #ccc; font-size: 14px; line-height: 1.7; padding-left: 18px; position: relative; margin-bottom: 4px; }
.action-tips ul li::before { content: "\2713"; color: #4CAF50; position: absolute; left: 0; font-weight: bold; }
.action-mistakes ul li::before { content: "\2717"; color: #f44336; position: absolute; left: 0; font-weight: bold; }
.action-modal-body .section-divider { height: 1px; background: #333; margin: 16px 0; }

/* ========== 全新饮食页样式 ========== */
#diet-page { padding-top: 70px; min-height: 100vh; }
.diet-nav { display: flex; justify-content: center; gap: 20px; padding: 15px; background: #fff; position: sticky; top: 0; z-index: 100; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.diet-nav-btn { background: none; border: none; font-size: 16px; font-weight: 600; color: var(--text-sub); padding: 10px 20px; cursor: pointer; border-radius: 8px; transition: all 0.3s ease; }
.diet-nav-btn.active { color: white; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.diet-nav-btn.training.active { background: #4CAF50; }
.diet-nav-btn.rest.active { background: #2196F3; }
.diet-section { display: none; padding: 20px; max-width: 900px; margin: 0 auto; }
.diet-section.active { display: block; animation: fadeIn 0.4s ease; }

.table-title-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.diet-actions { display: flex; gap: 10px; }
.btn-outline-small { background: transparent; border: 1px solid var(--primary); color: var(--primary); padding: 6px 14px; border-radius: 16px; font-size: 13px; cursor: pointer; transition: all 0.2s; font-weight: 600; }
.btn-outline-small:hover { background: var(--primary); color: #fff; }

.diet-table-wrapper { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.06); margin-bottom: 20px; }
.diet-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.diet-table thead th { padding: 14px 12px; text-align: left; font-weight: 700; font-size: 13px; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 2px solid #e0e0e0; }
.diet-table thead tr { background: #f8f9fa; color: #555; }
.diet-table tbody td { padding: 14px 12px; border-bottom: 1px solid #f0f0f0; vertical-align: middle; line-height: 1.5; }
.diet-table tbody tr:last-child td { border-bottom: none; }
.diet-table tbody tr:hover { background: #f8faf8; }

.type-badge { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 700; white-space: nowrap; }
.badge-leg { background: #e8f5e9; color: #2e7d32; }
.badge-chest { background: #e3f2fd; color: #1565c0; }
.badge-shoulder { background: #fff3e0; color: #e65100; }
.badge-rest { background: #eceff1; color: #546e7a; }
.badge-aerobic { background: #fce4ec; color: #c62828; }
.data-highlight { font-weight: 800; font-size: 14px; }
.data-cal { color: #e65100; }
.data-protein { color: #1565c0; }
.dash { color: #bbb; font-size: 18px; }

.rest-table-wrapper { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.06); margin-bottom: 20px; }
.rest-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.rest-table thead th { padding: 16px; text-align: left; background: #f0f7ff; color: #1565c0; font-weight: 700; font-size: 13px; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 2px solid #bbdefb; }
.rest-table tbody td { padding: 16px; border-bottom: 1px solid #f0f0f0; vertical-align: top; line-height: 1.6; }
.rest-table tbody tr:last-child td { border-bottom: none; }
.rest-table tbody tr:hover { background: #f8fbff; }
.rest-table .section-label { font-weight: 700; color: #1565c0; display: block; margin-bottom: 4px; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; }

/* 缺口提示面板 */
.tip-card { background: #fff; border-radius: 12px; padding: 20px; margin-top: 20px; box-shadow: 0 4px 6px rgba(0,0,0,0.02); border-left: 4px solid #FF9800; }
.tip-card h3 { font-size: 16px; color: #333; margin-bottom: 10px; display: flex; align-items: center; gap: 8px; }
body.dark-theme .tip-card h3 { color: #e0e0e0; }

.deficit-card { border-left-color: #ff4500; }
.deficit-info { display: flex; align-items: center; justify-content: space-between; margin-top: 15px; flex-wrap: wrap; gap: 10px; }
.d-item { text-align: center; flex: 1; min-width: 100px; background: #f9f9f9; padding: 15px; border-radius: 12px; border: 1px solid #eaeaea; }
body.dark-theme .d-item { background: #2c2c2e; border-color: #38383a; }
.d-label { font-size: 12px; color: var(--text-sub); margin-bottom: 5px; }
.d-value { font-size: 24px; font-weight: bold; }
.d-operator { font-size: 24px; color: #ccc; font-weight: bold; }
.deficit-note { font-size: 13px; margin-top: 15px; color: #888; text-align: center; }

/* 编辑排期列表样式 */
.schedule-row { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #eee; }
body.dark-theme .schedule-row { border-bottom-color: #333; }
.schedule-row:last-child { border-bottom: none; }
.schedule-row .day-label { font-weight: bold; width: 50px; color: var(--text-main); }
.schedule-row .mini-select { padding: 6px 8px; border-radius: 6px; border: 1px solid #ccc; background: #f9f9f9; outline: none; font-size: 14px; }
body.dark-theme .schedule-row .mini-select { background: #222; border-color: #444; color: #fff; }
.schedule-row .part-input { width: 120px; text-align: left; background: #f9f9f9; }
body.dark-theme .schedule-row .part-input { background: #222; }

/* ========== 新版数据统计页样式 ========== */
#stats-page .stats-wrapper { max-width: 800px; margin: 0 auto; padding: 80px 20px 40px; }
.stats-page-title { font-size: 28px; font-weight: 700; margin-bottom: 24px; color: var(--text-main); }
.stats-card { background: #fff; border-radius: 16px; padding: 24px; margin-bottom: 24px; box-shadow: 0 4px 15px rgba(0,0,0,0.04); border: 1px solid #eaeaea; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #f0f0f0; padding-bottom: 12px; }
body.dark-theme .card-header { border-bottom-color: #38383a; }
.card-header h3 { font-size: 18px; margin: 0; color: var(--text-main); font-weight: 600; }
.go-settings-btn { background: #f0f0f5; color: #0071e3; border: none; padding: 6px 14px; border-radius: 16px; font-size: 13px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
body.dark-theme .go-settings-btn { background: #2c2c2e; }
.go-settings-btn:hover { background: #e0e0e5; }
.empty-state { text-align: center; color: var(--text-sub); padding: 30px 0; font-size: 14px; }

/* 身体数据网格 */
.analysis-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 10px; }
.analysis-item { background: #f9f9f9; padding: 20px; border-radius: 12px; text-align: center; }
body.dark-theme .analysis-item { background: #2c2c2e; }
.analysis-item .data-value { font-size: 32px; font-weight: 700; color: var(--text-main); margin-bottom: 8px; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
.analysis-item .data-value .unit { font-size: 14px; color: var(--text-sub); font-weight: 500; }
.analysis-item .data-label { font-size: 13px; color: var(--text-sub); font-weight: 500; }
.text-accent { color: #ff4500 !important; }
.text-green { color: #34c759 !important; }
.text-red { color: #ff3b30 !important; }
.status-text { font-weight: 700; padding: 2px 6px; border-radius: 6px; font-size: 12px; }
.stats-note { font-size: 12px; color: #888; margin-top: 15px; text-align: right; }

/* 训练记录模板库 */
.routine-list { display: grid; grid-template-columns: 1fr; gap: 16px; }
.routine-card { background: #f8f9fa; border-radius: 12px; padding: 16px; border: 1px solid #eaeaea; transition: all 0.3s ease; }
body.dark-theme .routine-card { background: #2c2c2e; border-color: #38383a; }
.routine-card.selected-card { border-color: var(--primary); box-shadow: 0 0 0 2px rgba(0, 122, 204, 0.15); }
body.dark-theme .routine-card.selected-card { border-color: var(--primary); box-shadow: 0 0 0 2px rgba(0, 122, 204, 0.3); }
.routine-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px dashed #ccc; padding-bottom: 10px; margin-bottom: 10px; }
body.dark-theme .routine-header { border-bottom-color: #555; }
.routine-header h4 { margin: 0; color: var(--primary); font-size: 16px; }
.routine-actions { display: flex; align-items: center; gap: 10px; }
.select-btn { padding: 4px 10px; font-size: 12px; border-radius: 12px; border: 1px solid var(--primary); background: transparent; color: var(--primary); cursor: pointer; transition: all 0.2s; font-weight: 600; }
.select-btn:hover { background: rgba(0, 122, 204, 0.08); }
.select-btn.active { background: var(--primary); color: #fff; }
.delete-btn { background: #ff3b30; color: #fff; border: none; border-radius: 50%; width: 24px; height: 24px; font-size: 14px; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.delete-btn:hover { background: #d32f2f; }
.routine-ex-item { display: flex; justify-content: space-between; font-size: 14px; margin-bottom: 6px; color: var(--text-main); }
.ex-data { color: var(--text-sub); font-weight: 500; }
.routine-footer { margin-top: 10px; padding-top: 10px; border-top: 1px dashed #ccc; font-size: 12px; color: #888; text-align: right; }
body.dark-theme .routine-footer { border-top-color: #555; }

/* 弹窗公共样式 */
.add-workout-modal { background: #fff; border-radius: 20px; width: 90%; max-width: 450px; max-height: 85vh; overflow-y: auto; transform: translateY(20px); transition: all 0.3s; padding: 24px; }
body.dark-theme .add-workout-modal { background: #1c1c1e; }
.login-modal-overlay.show .add-workout-modal { transform: translateY(0); }
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.modal-header h3 { margin: 0; font-size: 20px; color: var(--text-main); }
.close-btn { background: transparent; border: none; font-size: 24px; color: var(--text-sub); cursor: pointer; }
.form-group { margin-bottom: 20px; text-align: left; }
.form-group label { display: block; font-size: 14px; color: var(--text-sub); margin-bottom: 8px; font-weight: 600; }
.form-select { width: 100%; padding: 12px; border-radius: 8px; border: 1px solid #d2d2d7; font-size: 15px; background: #f5f5f7; outline: none; }
body.dark-theme .form-select { background: #2c2c2e; border-color: #38383a; color: #fff; }

/* 目标部位复选框 */
.part-checkbox-group { display: flex; flex-wrap: wrap; gap: 15px; }

.exercise-select-list { max-height: 300px; overflow-y: auto; padding-right: 10px; }
.exercise-select-item { background: #f9f9f9; padding: 12px; border-radius: 8px; margin-bottom: 8px; border: 1px solid #eaeaea; }
body.dark-theme .exercise-select-item { background: #2c2c2e; border-color: #38383a; }
.checkbox-label { display: flex; align-items: center; gap: 10px; cursor: pointer; margin: 0 !important; color: var(--text-main) !important; font-size: 15px !important; }
.checkbox-label input { width: 18px; height: 18px; cursor: pointer; }
.ex-inputs { display: flex; align-items: center; gap: 8px; margin-top: 12px; padding-left: 28px; }
.mini-input { width: 60px; padding: 6px; border-radius: 6px; border: 1px solid #ccc; text-align: center; }
body.dark-theme .mini-input { background: #1c1c1e; border-color: #555; color: #fff; }
.unit { font-size: 12px; color: var(--text-sub); }
.modal-footer { margin-top: 24px; }
.btn-primary { width: 100%; background: #0071e3; color: #fff; border: none; padding: 14px; border-radius: 12px; font-size: 16px; font-weight: 600; cursor: pointer; }
.btn-primary:hover { background: #0058b0; }

/* ========== 全新设置页样式 (Apple Style) ========== */
#settings-page .settings-wrapper { max-width: 600px; margin: 0 auto; padding: 80px 20px 40px; }
.settings-title { font-size: 28px; font-weight: 700; margin-bottom: 24px; color: var(--text-main); }
.settings-group { background: #fff; border-radius: 12px; margin-bottom: 24px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
body.dark-theme .settings-group { background: #1c1c1e; }
.settings-item { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #f0f0f0; transition: background 0.2s; }
body.dark-theme .settings-item { border-bottom-color: #2c2c2e; }
.settings-item:last-child { border-bottom: none; }
.settings-item.clickable { cursor: pointer; }
.settings-item.clickable:hover { background: #f9f9f9; }
body.dark-theme .settings-item.clickable:hover { background: #2c2c2e; }
.item-left { display: flex; align-items: center; gap: 12px; font-size: 16px; color: var(--text-main); }
.item-left .icon { font-size: 20px; }
.profile-item { padding: 20px; }
.profile-avatar { width: 60px; height: 60px; background: #f0f0f0; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 30px; margin-right: 16px; }
body.dark-theme .profile-avatar { background: #2c2c2e; }
.profile-info { flex: 1; }
.profile-name { font-size: 20px; font-weight: 600; margin-bottom: 4px; color: var(--text-main); }
.profile-email { font-size: 14px; color: var(--text-sub); }
.btn-primary-small { background: #0071e3; color: #fff; border: none; padding: 6px 16px; border-radius: 16px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
.btn-primary-small:hover { background: #0058b0; }
.text-danger { color: #ff3b30 !important; font-weight: 500; }
.arrow { color: #c7c7cc; font-size: 20px; }

/* 个人资料输入框样式 */
.profile-input-item { display: flex; justify-content: space-between; align-items: center; }
.profile-input-item label { font-size: 16px; color: var(--text-main); font-weight: 500; }
.profile-input-item input, .profile-input-item select { text-align: right; border: none; background: transparent; font-size: 16px; color: var(--text-sub); outline: none; width: 160px; font-family: inherit; appearance: none; }
.profile-input-item select { cursor: pointer; direction: rtl; }
body.dark-theme .profile-input-item input, body.dark-theme .profile-input-item select { color: #aaa; }
body.dark-theme .profile-input-item select option { background: #1c1c1e; color: #fff; }

/* 苹果风格拨动开关 */
.apple-switch { position: relative; display: inline-block; width: 50px; height: 30px; }
.apple-switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background-color: #e9e9ea; transition: .4s; border-radius: 30px; }
body.dark-theme .slider { background-color: #38383a; }
.slider:before { position: absolute; content: ""; height: 26px; width: 26px; left: 2px; bottom: 2px; background-color: white; transition: .4s; border-radius: 50%; box-shadow: 0 2px 4px rgba(0,0,0,0.2); }
input:checked + .slider { background-color: #34c759; }
input:checked + .slider:before { transform: translateX(20px); }

/* ========== 登录弹窗样式 ========== */
.login-modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100vh; background: rgba(0,0,0,0.4); backdrop-filter: blur(8px); z-index: 3000; display: flex; align-items: center; justify-content: center; opacity: 0; visibility: hidden; transition: all 0.4s ease; }
.login-modal-overlay.show { opacity: 1; visibility: visible; }
.login-modal { background: #ffffff; border-radius: 24px; padding: 40px 32px; width: 90%; max-width: 400px; text-align: center; transform: translateY(20px); transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1); box-shadow: 0 24px 48px rgba(0,0,0,0.12); position: relative; }
body.dark-theme .login-modal { background: #1c1c1e; }
.login-modal-overlay.show .login-modal { transform: translateY(0); }
.login-modal-close { position: absolute; top: 16px; right: 16px; width: 32px; height: 32px; border-radius: 50%; background: #f5f5f7; border: none; font-size: 18px; color: #1d1d1f; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: background 0.2s; }
body.dark-theme .login-modal-close { background: #2c2c2e; color: #fff; }
.login-modal-close:hover { background: #e5e5ea; }
.login-header h2 { font-size: 24px; font-weight: 600; color: #1d1d1f; margin: 0 0 8px 0; }
body.dark-theme .login-header h2 { color: #fff; }
.login-header p { font-size: 14px; color: #86868b; margin: 0 0 28px 0; }
.login-form .input-wrapper { margin-bottom: 16px; }
.login-form input { width: 100%; padding: 14px 16px; border-radius: 12px; border: 1px solid #d2d2d7; font-size: 15px; background: #f5f5f7; transition: all 0.2s ease; outline: none; color: #1d1d1f; }
body.dark-theme .login-form input { background: #2c2c2e; border-color: #38383a; color: #fff; }
.login-form input::placeholder { color: #86868b; }
.login-form input:focus { border-color: #0071e3; background: #fff; box-shadow: 0 0 0 4px rgba(0,113,227,0.1); }
body.dark-theme .login-form input:focus { background: #1c1c1e; }
.btn-login { width: 100%; padding: 14px; border-radius: 12px; background: #0071e3; color: #fff; font-size: 16px; font-weight: 600; border: none; cursor: pointer; margin-top: 8px; transition: all 0.2s ease; }
.btn-login:hover:not(:disabled) { background: #0077ed; transform: scale(0.98); }
.btn-login:active:not(:disabled) { background: #0060c0; }
.btn-login:disabled { background: #99c8f6; cursor: not-allowed; }
.login-footer { margin-top: 24px; font-size: 13px; color: #86868b; display: flex; justify-content: center; gap: 12px; }
.login-footer a { color: #0071e3; text-decoration: none; transition: color 0.2s; }
.login-footer a:hover { color: #0058b0; text-decoration: underline; }

/* ========== 登录弹窗标签切换 ========== */
.auth-tabs { display: flex; justify-content: center; gap: 8px; margin-bottom: 24px; border-bottom: 1px solid #e5e5ea; padding-bottom: 12px; }
.auth-tab { padding: 8px 16px; font-size: 14px; font-weight: 500; color: #86868b; cursor: pointer; border-radius: 8px; transition: all 0.2s; user-select: none; }
.auth-tab:hover { color: #0071e3; background: rgba(0,113,227,0.05); }
.auth-tab.active { color: #0071e3; background: rgba(0,113,227,0.1); }
body.dark-theme .auth-tabs { border-bottom-color: #38383a; }
body.dark-theme .auth-tab { color: #8e8e93; }
body.dark-theme .auth-tab.active { color: #0a84ff; background: rgba(10,132,255,0.15); }

/* 验证码行 */
.captcha-row { display: flex; gap: 10px; align-items: center; }
.captcha-row .captcha-input { flex: 1; }
.captcha-img { width: 100px; height: 40px; border-radius: 8px; cursor: pointer; object-fit: cover; border: 1px solid #d2d2d7; }
.captcha-placeholder { width: 100px; height: 40px; border-radius: 8px; background: #f5f5f7; border: 1px dashed #d2d2d7; display: flex; align-items: center; justify-content: center; font-size: 12px; color: #86868b; cursor: pointer; }
body.dark-theme .captcha-img { border-color: #38383a; }
body.dark-theme .captcha-placeholder { background: #2c2c2e; border-color: #38383a; color: #8e8e93; }

/* 验证码发送行 */
.code-row { display: flex; gap: 10px; align-items: center; }
.code-row .code-input { flex: 1; }
.btn-send-code { padding: 12px 14px; border-radius: 10px; border: 1px solid #0071e3; background: #fff; color: #0071e3; font-size: 13px; font-weight: 500; cursor: pointer; white-space: nowrap; transition: all 0.2s; }
.btn-send-code:hover:not(:disabled) { background: #0071e3; color: #fff; }
.btn-send-code:disabled { opacity: 0.5; cursor: not-allowed; border-color: #99c8f6; color: #99c8f6; }
body.dark-theme .btn-send-code { background: #1c1c1e; }

/* ========== 手机适配 ========== */
@media (max-width: 768px) {
  #home-page #header h1 { font-size: 2em; }
  #home-page #content-card { flex-direction: column; min-height: auto; }
  #home-page #card-text { flex: auto; width: 100%; padding: 30px 20px; border-right: none; border-bottom: 1px solid #f0f0f0; }
  body.dark-theme #home-page #card-text { border-bottom-color: #38383a; }
  #home-page #card-image { flex: auto; width: 100%; min-height: 250px; }
  #home-page #header { margin-top: 50px; }
  .gym-nav-btn { font-size: 12px; padding: 10px 0; }
  .exercise-item { flex-direction: column; align-items: flex-start; gap: 8px; }
  .exercise-item .weight-input { width: 100%; }
  .diet-table, .rest-table { font-size: 12px; }
  .diet-table thead th, .diet-table tbody td, .rest-table thead th, .rest-table tbody td { padding: 10px 8px; }
  .type-badge { padding: 3px 8px; font-size: 11px; }
  #settings-page .settings-wrapper, #stats-page .stats-wrapper { padding-top: 60px; }
  .analysis-item .data-value { font-size: 26px; }
  .table-title-row { flex-direction: column; align-items: flex-start; gap: 10px; }
}

@media (max-width: 480px) {
  .diet-table-wrapper, .rest-table-wrapper { border-radius: 12px; overflow-x: auto; }
  .diet-table { min-width: 700px; }
  .rest-table { min-width: 500px; }
  .routine-actions { flex-direction: column; align-items: flex-end; gap: 6px; }
}

/* ========== AI 体重预测样式 ========== */
.prediction-result { padding: 16px 0; }
.prediction-main { display: flex; align-items: center; justify-content: center; gap: 20px; margin-bottom: 16px; }
.prediction-item { text-align: center; }
.prediction-value { font-size: 32px; font-weight: 700; color: #1d1d1f; }
.prediction-value.up { color: #ff3b30; }
.prediction-value.down { color: #34c759; }
.prediction-label { font-size: 13px; color: #86868b; margin-top: 4px; }
.prediction-arrow { font-size: 24px; color: #c7c7cc; }
.prediction-change { text-align: center; font-size: 18px; font-weight: 600; margin-bottom: 8px; }
.prediction-change.up { color: #ff3b30; }
.prediction-change.down { color: #34c759; }
.prediction-hint { text-align: center; font-size: 12px; color: #86868b; margin-top: 12px; }
body.dark-theme .prediction-value { color: #fff; }
body.dark-theme .prediction-label { color: #8e8e93; }
body.dark-theme .prediction-arrow { color: #555; }
body.dark-theme .prediction-hint { color: #8e8e93; }


/* ========== 智能热量计算 ========== */
.calorie-form {
  padding: 15px;
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.form-item {
  flex: 1;
}

.form-item label {
  display: block;
  font-size: 13px;
  color: #888;
  margin-bottom: 5px;
}

.form-item input,
.form-item select {
  width: 100%;
  padding: 10px;
  border: 1px solid #333;
  border-radius: 8px;
  background: #1a1a1a;
  color: #fff;
  font-size: 14px;
}

.calorie-result {
  padding: 15px;
  border-top: 1px solid #333;
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.result-item {
  text-align: center;
}

.result-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.result-value.text-accent {
  color: #ff6b35;
}

.result-value.text-green {
  color: #4caf50;
}

.result-label {
  font-size: 12px;
  color: #888;
  margin-top: 5px;
}


/* ========== 智能热量计算 ========== */
.calorie-form {
  padding: 15px;
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.form-item {
  flex: 1;
}

.form-item label {
  display: block;
  font-size: 13px;
  color: #888;
  margin-bottom: 5px;
}

.form-item input,
.form-item select {
  width: 100%;
  padding: 10px;
  border: 1px solid #333;
  border-radius: 8px;
  background: #1a1a1a;
  color: #fff;
  font-size: 14px;
}

.calorie-result {
  padding: 15px;
  border-top: 1px solid #333;
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.result-item {
  text-align: center;
}

.result-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.result-value.text-accent {
  color: #ff6b35;
}

.result-value.text-green {
  color: #4caf50;
}

.result-label {
  font-size: 12px;
  color: #888;
  margin-top: 5px;
}
</style>