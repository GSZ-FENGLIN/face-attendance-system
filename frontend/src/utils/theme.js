// 人民币配色方案
export const rmbThemes = [
  {
    name: '1元 · 橄榄黄绿',
    gradient: ['#6b7a30', '#8b9a46', '#c5d68e'],
    primary: '#8b9a46',
    light3: '#a8b85a',
    light5: '#c5d68e',
    light7: '#dae3b8',
    light8: '#e8edd4',
    light9: '#f2f5e8',
    dark2: '#6b7a30'
  },
  {
    name: '5元 · 紫',
    gradient: ['#4a2c6e', '#6b3fa0', '#b088b0'],
    primary: '#6b3fa0',
    light3: '#8b5e83',
    light5: '#b088b0',
    light7: '#d0b8d0',
    light8: '#e4d4e4',
    light9: '#f0eaf0',
    dark2: '#4a2c6e'
  },
  {
    name: '10元 · 蓝灰',
    gradient: ['#1a2639', '#3d5a80', '#7fa1c4'],
    primary: '#3d5a80',
    light3: '#5a7d9a',
    light5: '#7fa1c4',
    light7: '#a8c5da',
    light8: '#c8dce8',
    light9: '#e3eef5',
    dark2: '#2c4663'
  },
  {
    name: '20元 · 金黄',
    gradient: ['#8f7318', '#c49a2d', '#e8c46a'],
    primary: '#c49a2d',
    light3: '#d4a843',
    light5: '#e8c46a',
    light7: '#f0d99a',
    light8: '#f5e8c4',
    light9: '#faf3e2',
    dark2: '#a07e20'
  },
  {
    name: '50元 · 绿',
    gradient: ['#1a4a2e', '#2d6a4f', '#52b788'],
    primary: '#2d6a4f',
    light3: '#40916c',
    light5: '#52b788',
    light7: '#95d5b2',
    light8: '#b7e4c7',
    light9: '#d8f3dc',
    dark2: '#1b4d38'
  },
  {
    name: '100元 · 枣红',
    gradient: ['#6b1010', '#c0392b', '#d98880'],
    primary: '#c0392b',
    light3: '#d55a4e',
    light5: '#d98880',
    light7: '#e8b5b0',
    light8: '#f0d3d0',
    light9: '#f8eae8',
    dark2: '#8b1a1a'
  }
]

export function getRandomTheme() {
  const index = Math.floor(Math.random() * rmbThemes.length)
  return rmbThemes[index]
}

export function applyTheme(theme) {
  const root = document.documentElement
  root.style.setProperty('--el-color-primary', theme.primary)
  root.style.setProperty('--el-color-primary-light-3', theme.light3)
  root.style.setProperty('--el-color-primary-light-5', theme.light5)
  root.style.setProperty('--el-color-primary-light-7', theme.light7)
  root.style.setProperty('--el-color-primary-light-8', theme.light8)
  root.style.setProperty('--el-color-primary-light-9', theme.light9)
  root.style.setProperty('--el-color-primary-dark-2', theme.dark2)
  root.style.setProperty('--brand-start', theme.gradient[0])
  root.style.setProperty('--brand-mid', theme.gradient[1])
  root.style.setProperty('--brand-end', theme.gradient[2])
}
