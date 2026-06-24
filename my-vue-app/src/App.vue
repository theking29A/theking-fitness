<script setup>
import { onMounted } from 'vue';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';

gsap.registerPlugin(ScrollTrigger);

onMounted(() => {
  // 初始加载动画
  gsap.from('.hero-animate', {
    y: 30,
    opacity: 0,
    duration: 1.2,
    stagger: 0.2,
    ease: 'power4.out'
  });

  // 滚动进入动画
  const cards = document.querySelectorAll('.animate-card');
  cards.forEach((card) => {
    gsap.from(card, {
      y: 60,
      opacity: 0,
      duration: 1,
      ease: 'power3.out',
      scrollTrigger: {
        trigger: card,
        start: 'top 88%',
        toggleActions: 'play none none reverse'
      }
    });
  });
});
</script>

<template>
  <div class="portfolio-wrapper">
    <header class="hero-section">
      <h1 class="hero-animate headline">热爱延续，<br>以此构建未来。</h1>
      <p class="hero-animate subtitle">Web 前端开发实习生 · 2027 届</p>
    </header>

    <main class="bento-grid">
      
      <section class="card animate-card experience-card">
        <div class="card-header">
          <span class="tag-label">Professional Experience</span>
          <h2 class="title">实战历练</h2>
        </div>
        <div class="card-body">
          <div class="exp-item">
            <div class="exp-head">
              <h3>全栈业务开发 (小程序/后台)</h3>
              <span class="badge">Internship</span>
            </div>
            <p class="exp-desc">深度参与企业级产品迭代，负责核心业务流程的前端实现。</p>
            <ul class="points">
              <li><strong>权限架构：</strong>实现总/分公司多级 RBAC 权限控制，解决复杂组织下的数据隔离。</li>
              <li><strong>核心逻辑：</strong>负责用户管理、资金提现模块，确保金融级业务交互的严谨与实时。</li>
              <li><strong>组件封装：</strong>基于业务需求封装高复用 UI 组件，提升团队开发效率。</li>
            </ul>
          </div>
        </div>
      </section>

      <section class="card animate-card project-card">
        <div class="card-header">
          <span class="tag-label">Featured Work</span>
          <h2 class="title">ThekingFitness</h2>
          <a href="https://theking29a.github.io/ThekingFitness/" target="_blank" class="visit-link">View Site ↗</a>
        </div>
        <div class="project-preview">
          <img src="./assets/a-1.png" alt="Project Preview" class="preview-img">
        </div>
      </section>

      <section class="card animate-card cert-card">
        <div class="card-header">
          <span class="tag-label">Languages</span>
          <h2 class="title">语言能力</h2>
        </div>
        <div class="cert-content">
          <div class="cert-box">
            <span class="icon">🇺🇸</span>
            <div class="text">
              <strong>CET-6 / CET-4</strong>
              <p>具备流畅的英文技术文档阅读与检索能力</p>
            </div>
          </div>
        </div>
      </section>

      <section class="card animate-card profile-card">
        <div class="card-header">
          <span class="tag-label">About Me</span>
          <h2 class="title">教育背景</h2>
        </div>
        <div class="card-body">
          <p class="school">滇池学院</p>
          <p class="major">计算机科学与技术 · 本科</p>
          <div class="bio-tags">
            <span># 自学能力强</span>
            <span># 成就感驱动</span>
          </div>
        </div>
      </section>

      <section class="card animate-card hobby-card">
        <div class="card-header">
          <span class="tag-label">Beyond Code</span>
          <h2 class="title">代码之外</h2>
        </div>
        <div class="hobby-content">
          <div class="hobby-tags">
            <span class="h-tag">🏋️ 健身</span>
            <span class="h-tag">🎬 视频剪辑</span>
            <span class="h-tag">🎸 吉他</span>
            <span class="h-tag">🏀 篮球</span>
          </div>
          <p class="hobby-quote">
            “用剪辑的逻辑培养<b>交互审美</b>，以健身的意志保持<b>极致自律</b>。”
          </p>
        </div>
      </section>

      <section class="card animate-card skills-card">
        <div class="card-header">
          <span class="tag-label">Tech Stack</span>
          <h2 class="title">技术驱动</h2>
        </div>
        <div class="skill-cloud">
          <div class="s-item highlight">Vue 3</div>
          <div class="s-item">JavaScript (ES6+)</div>
          <div class="s-item">CSS3 / Scss</div>
          <div class="s-item">Vite</div>
          <div class="s-item">GSAP</div>
          <div class="s-item">Git</div>
        </div>
      </section>

      <section class="card animate-card contact-card">
        <div class="card-header">
          <span class="tag-label">Contact</span>
          <h2 class="title">建立联系</h2>
        </div>
        <div class="contact-methods">
          <p>正在寻找 2024-2025 实习机会</p>
          <div class="cta-group">
            <a href="https://github.com/theking29a" target="_blank" class="cta-btn github">GitHub</a>
            <a href="mailto:your-email@example.com" class="cta-btn mail">Email</a>
          </div>
        </div>
      </section>

    </main>

    <footer class="footer">
      <p>© 2024 Built with Passion by Theking</p>
    </footer>
  </div>
</template>

<style scoped>
/* 全局重置 */
:global(body) {
  margin: 0;
  padding: 0;
  background-color: #ffffff;
  color: #1d1d1f;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", sans-serif;
  -webkit-font-smoothing: antialiased;
}

.portfolio-wrapper {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Header */
.hero-section {
  padding: 100px 0 60px;
  text-align: center;
}

.headline {
  font-size: clamp(2.5rem, 8vw, 4.2rem);
  font-weight: 700;
  letter-spacing: -0.03em;
  line-height: 1.1;
}

.subtitle {
  font-size: 1.4rem;
  color: #86868b;
  margin-top: 20px;
}

/* Grid 布局 */
.bento-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-auto-rows: minmax(260px, auto);
  gap: 20px;
  margin-bottom: 80px;
}

@media (max-width: 768px) {
  .bento-grid { grid-template-columns: 1fr; }
}

/* 通用卡片样式 */
.card {
  background-color: #f5f5f7;
  border-radius: 28px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  transition: all 0.5s cubic-bezier(0.2, 0.8, 0.2, 1);
  border: 1px solid transparent;
}

.card:hover {
  transform: translateY(-4px) scale(1.01);
  background-color: #ffffff;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
  border-color: rgba(0,0,0,0.03);
}

.tag-label {
  font-size: 0.7rem;
  font-weight: 600;
  color: #86868b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 6px;
  display: block;
}

.title {
  font-size: 1.6rem;
  font-weight: 600;
  margin: 0 0 20px 0;
}

/* 经历卡片 */
.experience-card { grid-column: span 2; }
@media (max-width: 768px) { .experience-card { grid-column: span 1; } }

.exp-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.badge {
  background: #e8e8ed;
  padding: 3px 8px;
  border-radius: 6px;
  font-size: 0.65rem;
  font-weight: 700;
}

.points {
  padding-left: 18px;
  color: #424245;
  line-height: 1.7;
  font-size: 0.95rem;
}

/* 作品卡片 */
.project-card { background: #000; color: #fff; position: relative; overflow: hidden; }
.project-card .title, .project-card .tag-label { color: #fff; }
.visit-link { color: #2997ff; text-decoration: none; font-size: 0.9rem; }
.project-preview { margin-top: 15px; border-radius: 12px; overflow: hidden; }
.preview-img { width: 100%; display: block; transition: 0.8s ease; }
.project-card:hover .preview-img { transform: scale(1.08); }

/* 语言能力卡片 */
.cert-box {
  display: flex;
  align-items: center;
  gap: 15px;
  background: rgba(0,0,0,0.03);
  padding: 15px;
  border-radius: 18px;
}
.cert-box .icon { font-size: 1.8rem; }
.cert-box strong { font-size: 1rem; display: block; }
.cert-box p { font-size: 0.8rem; color: #86868b; margin: 2px 0 0; }

/* 爱好卡片 */
.hobby-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 15px; }
.h-tag { background: #fff; padding: 6px 12px; border-radius: 10px; font-size: 0.85rem; font-weight: 500; }
.hobby-quote { font-size: 0.9rem; color: #515154; line-height: 1.5; border-left: 3px solid #d2d2d7; padding-left: 12px; }

/* 技术栈 */
.skill-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.s-item { padding: 6px 14px; background: #e8e8ed; border-radius: 8px; font-size: 0.85rem; font-weight: 500; }
.s-item.highlight { background: #0071e3; color: #fff; }

/* 其他 */
.school { font-size: 1.3rem; font-weight: 600; margin: 0; }
.major { color: #86868b; margin: 4px 0 15px; font-size: 0.95rem; }
.bio-tags { display: flex; gap: 8px; flex-wrap: wrap; }
.bio-tags span { font-size: 0.8rem; background: #e8e8ed; padding: 4px 10px; border-radius: 6px; }

.cta-group { display: flex; gap: 10px; margin-top: 15px; }
.cta-btn { flex: 1; text-align: center; padding: 12px; border-radius: 12px; text-decoration: none; color: #fff; font-weight: 600; font-size: 0.9rem; }
.github { background: #1d1d1f; }
.mail { background: #0071e3; }

.footer { text-align: center; padding: 40px 0; color: #86868b; font-size: 0.8rem; }
</style>