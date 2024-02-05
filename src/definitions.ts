export interface AudioDRMPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
