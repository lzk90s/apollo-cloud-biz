import md5 from 'md5';
import { serviceAdapter } from '@/utils';
import { userInfoAdapter, routeInfoAdapter } from '../adapter/auth';
import { request } from '../request';

/**
 * 获取验证码
 * @param phone - 手机号
 * @returns - 返回boolean值表示是否发送成功
 */
export function fetchSmsCode(phone: string) {
  return request.post<boolean>('/getSmsCode', { phone });
}

/**
 * 登录
 * @param userName - 用户名
 * @param password - 密码
 */
export async function fetchLogin(phone: string, pwdOrCode: string) {
  const param = new FormData();
  param.append('username', phone);
  param.append('password', md5(pwdOrCode));
  param.append('grant_type', 'password');
  param.append('grant_type', 'all');
  param.append('client_id', 'web');
  param.append('client_secret', 'web-secret');
  return request.post<ApiAuth.Token>('/api/uaa/oauth/token', param);
}

/** 获取用户信息 */
export async function fetchUserInfo() {
  const res = await request.get<ApiAuth.Adapter.UserInfoVO>('/api/permission/user/info');
  return serviceAdapter(userInfoAdapter, res);
}

/**
 * 获取用户路由数据
 * @param userId - 用户id
 * @description 后端根据用户id查询到对应的角色类型，并将路由筛选出对应角色的路由数据返回前端
 */
export async function fetchUserRoutes(userId: string) {
  const res = await request.get<AuthRoute.Route[]>('/api/permission/user/nav-tree');
  return serviceAdapter(routeInfoAdapter, res);
}

/**
 * 刷新token
 * @param refreshToken
 */
export function fetchUpdateToken(refreshToken: string) {
  return request.post<ApiAuth.Token>('/updateToken', { refreshToken });
}
