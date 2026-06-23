sz = 5; sig = 2
img_gsf = cv.GaussianBlur(img, ksize=(sz, sz), sig)
cv.imwrite('w-gs.png', img_gsf)

# Laplacian算子
t0 = img_gsf.copy().astype(np.double)    # 因为可能存在负数结果，将img_gsf转换为double类型
t = cv.Laplacian(t0, -1, ksize=3)

# 为了便于显示，将滤波结果归一化至[0,255]
maxt = np.max(t);   mint = np.min(t)
t2 = (t - mint)/(maxt-mint) * 255

cv.imwrite('w-lp.png', t2)    # 保存拉普拉斯滤波结果
cv.imwrite('w-le.png', img_gsf-t)    # 保存边缘增强结果