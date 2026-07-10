import os
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'

import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout
import json

def generate_sample_data():
    """生成模拟体重数据（用于训练）"""
    np.random.seed(42)
    
    # 模拟100个用户的体重变化数据
    all_data = []
    
    for _ in range(100):
        # 初始体重 60-90kg
        base_weight = np.random.uniform(60, 90)
        
        # 生成30天的体重数据（带趋势和波动）
        days = 30
        trend = np.random.uniform(-0.3, 0.3)  # 减重或增重趋势
        weights = []
        
        for i in range(days):
            noise = np.random.normal(0, 0.5)  # 每日波动
            weight = base_weight + trend * i + noise
            weights.append(weight)
        
        all_data.append(weights)
    
    return np.array(all_data)

def prepare_data(data, lookback=7):
    """准备训练数据"""
    X, y = [], []
    
    for sequence in data:
        for i in range(len(sequence) - lookback):
            X.append(sequence[i:i+lookback])
            y.append(sequence[i+lookback])
    
    X = np.array(X)
    y = np.array(y)
    
    # 归一化
    X_mean = np.mean(X)
    X_std = np.std(X)
    X_normalized = (X - X_mean) / X_std
    y_normalized = (y - X_mean) / X_std
    
    return X_normalized, y_normalized, X_mean, X_std

def build_model(lookback=7):
    """构建 LSTM 模型"""
    model = Sequential([
        LSTM(64, return_sequences=True, input_shape=(lookback, 1)),
        Dropout(0.2),
        LSTM(32, return_sequences=False),
        Dropout(0.2),
        Dense(16, activation='relu'),
        Dense(1)
    ])
    
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
        loss='mse',
        metrics=['mae']
    )
    
    return model

def train_model():
    """训练体重预测模型"""
    print('生成训练数据...')
    data = generate_sample_data()
    
    print('准备数据...')
    X, y, X_mean, X_std = prepare_data(data, lookback=7)
    
    #  reshape for LSTM
    X = X.reshape(X.shape[0], X.shape[1], 1)
    
    # 划分训练集和测试集
    split = int(0.8 * len(X))
    X_train, X_test = X[:split], X[split:]
    y_train, y_test = y[:split], y[split:]
    
    print(f'训练集: {len(X_train)} 条')
    print(f'测试集: {len(X_test)} 条')
    
    # 构建模型
    model = build_model(lookback=7)
    model.summary()
    
    # 训练
    print('开始训练...')
    history = model.fit(
        X_train, y_train,
        epochs=50,
        batch_size=32,
        validation_split=0.2,
        verbose=1
    )
    
    # 评估
    loss, mae = model.evaluate(X_test, y_test, verbose=0)
    print(f'测试集 MAE: {mae:.4f}')
    
    # 保存模型
    model_dir = os.path.join(os.path.dirname(__file__), '..', 'model')
    os.makedirs(model_dir, exist_ok=True)
    model_path = os.path.join(model_dir, 'weight_model.h5')
    model.save(model_path)
    print(f'模型已保存到: {model_path}')
    
    # 保存归一化参数
    params = {
        'mean': float(X_mean),
        'std': float(X_std)
    }
    params_path = os.path.join(model_dir, 'params.json')
    with open(params_path, 'w') as f:
        json.dump(params, f)
    print(f'参数已保存到: {params_path}')
    
    return model

if __name__ == '__main__':
    train_model()
