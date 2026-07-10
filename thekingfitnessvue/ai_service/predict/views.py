from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json
import numpy as np
import os

# 尝试加载模型
model = None
try:
    import tensorflow as tf
    model_path = os.path.join(os.path.dirname(__file__), '..', 'model', 'weight_model.h5')
    if os.path.exists(model_path):
        model = tf.keras.models.load_model(model_path)
except Exception as e:
    print(f'模型加载失败: {e}')

@csrf_exempt
def predict_weight(request):
    """体重趋势预测接口"""
    if request.method != 'POST':
        return JsonResponse({'code': 405, 'message': '只接受POST请求'})
    
    try:
        data = json.loads(request.body)
        history = data.get('history', [])  # 历史体重列表，如 [70, 69.5, 69, 68.8]
        
        if len(history) < 7:
            return JsonResponse({
                'code': 400, 
                'message': '需要至少7天的历史体重数据'
            })
        
        # 如果有模型，用模型预测
        if model is not None:
            # 数据预处理
            arr = np.array(history[-30:])  # 取最近30天
            if len(arr) < 30:
                # 不足30天，补全
                arr = np.pad(arr, (30 - len(arr), 0), mode='edge')
            
            # 归一化
            mean = np.mean(arr)
            std = np.std(arr) if np.std(arr) > 0 else 1
            normalized = (arr - mean) / std
            
            # 预测
            input_data = normalized.reshape(1, 30, 1)
            pred = model.predict(input_data, verbose=0)
            predicted_weight = float(pred[0][0] * std + mean)
        else:
            # 没有模型，用简单线性回归预测
            predicted_weight = simple_linear_predict(history)
        
        # 计算趋势
        trend = 'up' if predicted_weight > history[-1] else 'down'
        
        return JsonResponse({
            'code': 200,
            'message': '预测成功',
            'data': {
                'current_weight': history[-1],
                'predicted_weight': round(predicted_weight, 2),
                'trend': trend,
                'change': round(predicted_weight - history[-1], 2)
            }
        })
        
    except Exception as e:
        return JsonResponse({'code': 500, 'message': f'预测失败: {str(e)}'})

def simple_linear_predict(history):
    """简单线性回归预测（备用方案）"""
    n = len(history)
    x = np.arange(n)
    y = np.array(history)
    
    # 最小二乘法
    slope = (n * np.sum(x * y) - np.sum(x) * np.sum(y)) / (n * np.sum(x**2) - np.sum(x)**2)
    intercept = (np.sum(y) - slope * np.sum(x)) / n
    
    # 预测下一天
    next_day = n
    prediction = slope * next_day + intercept
    
    return prediction
